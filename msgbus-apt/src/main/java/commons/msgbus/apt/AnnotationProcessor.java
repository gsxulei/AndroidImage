package commons.msgbus.apt;

import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import commons.msgbus.MsgReceiver;

public class AnnotationProcessor extends AbstractProcessor
{
	private Messager mLog;
	private Filer mFiler;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv)
	{
		super.init(processingEnv);
		mLog=processingEnv.getMessager();
		mFiler=processingEnv.getFiler();
	}

	@Override
	public SourceVersion getSupportedSourceVersion()
	{
		return SourceVersion.latestSupported();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes()
	{
		Set<String> types=new LinkedHashSet<>();
		types.add(MsgReceiver.class.getCanonicalName());
		return types;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,RoundEnvironment roundEnv)
	{
		if(annotations.size()<=0)
		{
			return false;
		}
		mLog.printMessage(Diagnostic.Kind.NOTE,"开始处理->"+annotations.size());

		int methodId=0;
		Map<String,JavaClassBean> map=new HashMap<>();

		Set<? extends Element> elements=roundEnv.getElementsAnnotatedWith(MsgReceiver.class);
		for(Element element : elements)
		{
			//因为 MsgReceiver 的作用对象是 METHOD，因此 element 可以直接转化为 ExecutableElement
			ExecutableElement executableElement=(ExecutableElement)element;
			TypeElement enclosingElement=(TypeElement)executableElement.getEnclosingElement();

			//getEnclosingElement 方法返回封装此 Element 的最里层元素
			//如果 Element 直接封装在另一个元素的声明中，则返回该封装元素
			//此处表示的即 Activity 类对象
			String className=enclosingElement.getSimpleName().toString();
			String packageName=enclosingElement.getEnclosingElement().toString();
			String methodName=executableElement.getSimpleName().toString();

			mLog.printMessage(Diagnostic.Kind.NOTE,"注解类型->"+element.getKind());
			mLog.printMessage(Diagnostic.Kind.NOTE,"包名->"+packageName);
			mLog.printMessage(Diagnostic.Kind.NOTE,"类名->"+className);
			mLog.printMessage(Diagnostic.Kind.NOTE,"方法名->"+executableElement.getModifiers()+methodName);

			JavaClassBean javaClassBean=map.get(packageName+"."+className);
			if(javaClassBean==null)
			{
				javaClassBean=new JavaClassBean();
				javaClassBean.className=className;
				javaClassBean.packageName=packageName;
				map.put(packageName+"."+className,javaClassBean);
			}
			MsgReceiver msgReceiver=element.getAnnotation(MsgReceiver.class);

			MethodBean methodBean=new MethodBean();
			methodBean.methodName=methodName;
			methodBean.msgId=msgReceiver.id();
			methodBean.methodId=methodId++;
			methodBean.sticky=msgReceiver.sticky();
			methodBean.priority=msgReceiver.priority();
			methodBean.threadType=msgReceiver.threadType();

			Set<Modifier> modifiers=executableElement.getModifiers();
			if(modifiers!=null&&modifiers.contains(Modifier.STATIC))
			{
				methodBean.staticMethod=true;
			}

			javaClassBean.methodBeans.add(methodBean);
		}

		//创建xxxMsgBusCaller.java文件
		for(Map.Entry<String,JavaClassBean> entry : map.entrySet())
		{
			JavaClassBean javaClassBean=entry.getValue();
			createMsgBusCallerJavaFile(javaClassBean);
		}

		//创建MsgBusCallerController.java文件
		createMsgBusCallerControllerJavaFile(map);
		return false;
	}

	private void createMsgBusCallerJavaFile(JavaClassBean bean)
	{
		String classNameTemplate="public class %s implements commons.msgbus.MsgBusCaller<%s>";
		String callIdTemplate="private void call_%d(%s object,MsgEvent event,int index){switch(index){";
		String callIdContentTemplate="{object.%s(event);}";
		String callIdStaticContentTemplate="{%s.%s(event);}";
		String callTemplate="public void call(%s object,MsgEvent event,int index){switch(event.id){";
		String callContentTemplate="case %d:{call_%d(object,event,index);}";
		try
		{
			String className=bean.className+"MsgBusCaller";
			String fullName=bean.packageName+"."+className;
			JavaFileObject fileObject=mFiler.createSourceFile(fullName);

			Writer writer=fileObject.openWriter();
			writer.write("package "+bean.packageName+";");
			writer.write("import commons.msgbus.MsgEvent;");
			writer.write("@SuppressWarnings(\"unchecked\")");
			writer.write(getStringFromTemplate(classNameTemplate,className,bean.className));
			writer.write("{");

			Map<Integer,StringBuilder> map=new HashMap<>();
			StringBuilder callStr=new StringBuilder(getStringFromTemplate(callTemplate,bean.className));
			for(MethodBean methodBean : bean.methodBeans)
			{
				StringBuilder callIdStr=map.get(methodBean.msgId);
				if(callIdStr==null)
				{
					callIdStr=new StringBuilder();
					callIdStr.append(getStringFromTemplate(callIdTemplate,methodBean.msgId,bean.className));
					map.put(methodBean.msgId,callIdStr);
					callStr.append(getStringFromTemplate(callContentTemplate,methodBean.msgId,methodBean.msgId));
				}
				callIdStr.append(getStringFromTemplate("case %d:",methodBean.methodId));
				String content;
				if(methodBean.staticMethod)
				{
					content=getStringFromTemplate(callIdStaticContentTemplate,bean.className,methodBean.methodName);
				}
				else
				{
					content=getStringFromTemplate(callIdContentTemplate,methodBean.methodName);
				}
				callIdStr.append(content);
				callIdStr.append("break;");
			}
			for(Map.Entry<Integer,StringBuilder> entry : map.entrySet())
			{
				StringBuilder callIdStr=entry.getValue();
				callIdStr.append("}}");
				writer.write(callIdStr.toString());
			}
			callStr.append("}}");
			writer.write(callStr.toString());
			writer.write("}");
			writer.flush();
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void createMsgBusCallerControllerJavaFile(Map<String,JavaClassBean> map)
	{
		try
		{
			String addCallerTemplate="MsgBus.addCaller(%s.class,new %sMsgBusCaller());";
			String addTargetTemplate="MsgBus.addTarget(new MsgTarget(%d,%d,%s,%d,%d,%s.class));";
			String initObjectTemplate="MsgBus.initObject(%s.class);";

			String className="MsgBusCallerController";
			String packageName="commons.msgbus";
			String fullName=packageName+"."+className;
			JavaFileObject fileObject=mFiler.createSourceFile(fullName);

			Writer writer=fileObject.openWriter();
			writer.write("package "+packageName+";");
			writer.write("import "+packageName+".MsgBus;");
			writer.write("import "+packageName+".MsgEvent;");
			writer.write("public class "+className);
			writer.write("{");
			writer.write("public static void init(){");

			StringBuilder initObjectStr=new StringBuilder();
			for(String name : map.keySet())
			{
				writer.write(getStringFromTemplate(addCallerTemplate,name,name));
				initObjectStr.append(getStringFromTemplate(initObjectTemplate,name));
			}

			StringBuilder addTargetStr=new StringBuilder();

			for(Map.Entry<String,JavaClassBean> entry : map.entrySet())
			{
				JavaClassBean javaClassBean=entry.getValue();
				String targetClassName=javaClassBean.packageName+"."+javaClassBean.className;
				for(MethodBean methodBean : javaClassBean.methodBeans)
				{
					addTargetStr.append(getStringFromTemplate(addTargetTemplate,methodBean.msgId,methodBean.methodId,
							methodBean.sticky+"",methodBean.priority,methodBean.threadType,targetClassName));
				}
			}
			writer.write(initObjectStr.toString());
			writer.write(addTargetStr.toString());

			writer.write("}");
			writer.write("}");
			writer.flush();
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static String getStringFromTemplate(String template,Object... args)
	{
		return String.format(Locale.US,template,args);
	}
}