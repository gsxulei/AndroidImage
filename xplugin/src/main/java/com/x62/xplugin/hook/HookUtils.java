package com.x62.xplugin.hook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

public class HookUtils
{
	public static void hook(Application application)
	{
		try
		{
			// 在这里进行Hook
			attachContext(application.getApplicationContext());
			//hookActivity(activity);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void attachContext(Context context) throws Exception
	{
		// 先获取到当前的ActivityThread对象
		Class<?> activityThreadClass=Class.forName("android.app.ActivityThread");
		Method currentActivityThreadMethod=activityThreadClass.getDeclaredMethod("currentActivityThread");
		currentActivityThreadMethod.setAccessible(true);

		// currentActivityThread是一个static函数所以可以直接invoke，不需要带实例参数
		Object currentActivityThread=currentActivityThreadMethod.invoke(null);

		// 拿到原始的 mInstrumentation字段
		Field mInstrumentationField=activityThreadClass.getDeclaredField("mInstrumentation");
		mInstrumentationField.setAccessible(true);
		Instrumentation mInstrumentation=(Instrumentation)mInstrumentationField.get(currentActivityThread);

		// 如果没有注入过,就执行替换
		if(!(mInstrumentation instanceof EvilInstrumentation))
		{
			// 创建代理对象
			Instrumentation evilInstrumentation=new EvilInstrumentation(mInstrumentation,context);
			// 偷梁换柱
			mInstrumentationField.set(currentActivityThread,evilInstrumentation);
		}
	}

	//	public static void hookActivity(Activity activity)
	//	{
	//		Field instrumentationField=null;
	//		try
	//		{
	//			// 此处使用Activity，不能使用this
	//			// this指向继承于BaseActivity的类，如NewsDetailActivity，getDeclaredField获取到的是声明的域
	//			instrumentationField=Activity.class.getDeclaredField("mInstrumentation");
	//		}
	//		catch(NoSuchFieldException e)
	//		{
	//			e.printStackTrace();
	//		}
	//		if(instrumentationField!=null)
	//		{
	//			// 设置为可访问，否则IllegalAccessException
	//			instrumentationField.setAccessible(true);
	//			Instrumentation originInstrumentation=null;
	//			try
	//			{
	//				// 获取Activity对象的Instrumentation对象
	//				originInstrumentation=(Instrumentation)instrumentationField.get(activity);
	//			}
	//			catch(IllegalAccessException e)
	//			{
	//				e.printStackTrace();
	//			}
	//
	//			// 通过查找想要“包装”的方法是否存在，判断能否可以被hook
	//			Method execStartActivityMethod=null;
	//			try
	//			{
	//				int SDK=Build.VERSION.SDK_INT;
	//				if(SDK<=15)
	//				{
	//					execStartActivityMethod=Instrumentation.class.getDeclaredMethod("execStartActivity",Context
	// .class,
	//							IBinder.class,IBinder.class,Activity.class,Intent.class,int.class);
	//				}
	//				else if(SDK>15)
	//				{
	//					execStartActivityMethod=Instrumentation.class.getDeclaredMethod("execStartActivity",Context
	// .class,
	//							IBinder.class,IBinder.class,Activity.class,Intent.class,int.class,Bundle.class);
	//				}
	//			}
	//			catch(NoSuchMethodException e)
	//			{
	//				e.printStackTrace();
	//			}
	//
	//			// hook
	//			if(originInstrumentation!=null&&execStartActivityMethod!=null)
	//			{
	//				try
	//				{
	//					instrumentationField.set(activity,new EvilInstrumentation(originInstrumentation,activity));
	//					System.err.println("hookActivity");
	//				}
	//				catch(IllegalAccessException e)
	//				{
	//					e.printStackTrace();
	//				}
	//			}
	//		}
	//	}
}