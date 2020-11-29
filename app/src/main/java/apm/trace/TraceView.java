package apm.trace;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import java.io.File;

import commons.utils.IOUtils;

public class TraceView
{
	private static final Handler HANDLER;
	private static final StackRecord record;
	private static TraceElement root=new TraceElement();
	private static long sIntervalUs;
	private static String tracePath;

	static
	{
		HandlerThread thread=new HandlerThread("StackTracker");
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
		HANDLER=new Handler(thread.getLooper());

		record=new StackRecord();
		record.start();
	}

	public static void start(String path)
	{
		start(path,1);
	}

	public static void start(String path,long intervalUs)
	{
		HANDLER.post(()->
		{
			root=new TraceElement();
			sIntervalUs=intervalUs;
			tracePath=getTracePath(path);
			record.setIntervalUs(intervalUs);
			record.startRecord();
		});
	}

	public static void stop()
	{
		HANDLER.post(()->
		{
			record.stopRecord();
			String text=root.toString();
			IOUtils.write(tracePath,text);
		});
	}

	public static void add(StackTraceElement[] elements,long time)
	{
		HANDLER.post(()->getMainStackTrace(root,elements,elements.length-1,time));
	}

	private static void getMainStackTrace(TraceElement trace,StackTraceElement[] elements,int index,long time)
	{
		if(index<0)
		{
			return;
		}

		if(trace.self==null)
		{
			trace.self=elements[index];
			trace.startTime=time;
			trace.endTime=time+sIntervalUs;

			getMainStackTrace(trace,elements,index-1,time);
			return;
		}

		if(same(trace.self,elements[index]))
		{
			trace.endTime=time;
			getMainStackTrace(trace,elements,index-1,time);
			return;
		}

		if(trace.children.size()==0)
		{
			TraceElement children=addChildren(trace,elements[index],time);
			getMainStackTrace(children,elements,index-1,time);
			return;
		}

		if(same(trace.self,elements[index+1]))
		{
			TraceElement last=trace.children.get(trace.children.size()-1);
			if(same(last.self,elements[index]))
			{
				last.endTime=time;
				trace.endTime=last.endTime;
				getMainStackTrace(last,elements,index-1,time);
				return;
			}
			TraceElement children=addChildren(trace,elements[index],time);
			getMainStackTrace(children,elements,index-1,time);
		}

		//TODO 还有其他情况吗？
	}

	private static TraceElement addChildren(TraceElement trace,StackTraceElement element,long time)
	{
		TraceElement children=new TraceElement();
		children.self=element;
		children.startTime=time;
		children.endTime=time+sIntervalUs;
		trace.endTime=children.endTime;
		trace.children.add(children);
		return children;
	}

	private static boolean same(StackTraceElement e1,StackTraceElement e2)
	{
		if(e1==null||e2==null)
		{
			return false;
		}

		String className1=e1.getClassName();
		String className2=e2.getClassName();
		if(TextUtils.isEmpty(className1)||TextUtils.isEmpty(className2))
		{
			return false;
		}

		String methodName1=e1.getMethodName();
		String methodName2=e2.getMethodName();
		if(TextUtils.isEmpty(methodName1)||TextUtils.isEmpty(methodName2))
		{
			return false;
		}
		return className1.equals(className2)&&methodName1.equals(methodName2);
	}

	private static String getTracePath(String path)
	{
		if(TextUtils.isEmpty(path)||path.charAt(0)!='/')
		{
			final File dir=Environment.getExternalStorageDirectory();
			if(TextUtils.isEmpty(path))
			{
				path="trace_view";
			}
			path=new File(dir,path).getAbsolutePath();
		}
		if(!path.endsWith(".trace"))
		{
			path+=".trace";
		}
		return path;
	}
}