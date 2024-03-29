package app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import commons.agent.BaseAgent;
import commons.image.ImageLoaderWrapper;

import apm.crash.CrashMonitor;
import commons.utils.DisplaySettings;
import commons.utils.PatchUtils;
import commons.widget.utils.TopActivity;
import image.PreviewAgent;

import com.x62.image.R;

import commons.network.Downloader;

import commons.utils.PathUtils;

/**
 * Created by GSXL on 2018-05-05.
 */

public class AndroidApplication extends Application
{
	//CountDownLatch mCountDownLatch=new CountDownLatch(1);

	@Override
	protected void attachBaseContext(Context base)
	{
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		//崩溃处理初始化
		//CrashHandler.getInstance().init(this,PathUtils.getCrashPath());
		CrashMonitor.init();

		//BlockMonitor.init();

		//设置屏幕宽度dpi
		//ScreenUtils.getInstance().setDpi();
		DisplaySettings.setDpi(this);

		ImageLoaderWrapper.setDefault(R.mipmap.ic_launcher);
		Downloader.setDownloadDir(PathUtils.getCachePath());

		fixProguard();

		PatchUtils.loadPatch(this);

		//BlockCanary.install(this,new BlockCanaryContext()).start();
		//CrashMonitor.addOnLoopListener(BlockCanaryFix::fix);

		//		ANRWatchDog watchDog=new ANRWatchDog();
		//		watchDog.setANRListener((error)->
		//		{
		//			StringBuilder msg=new StringBuilder();
		//			for(StackTraceElement element : error.getCause().getStackTrace())
		//			{
		//				msg.append("\t");
		//				msg.append(element.toString());
		//				msg.append("\n");
		//			}
		//			Logger.e(msg.toString());
		//		});
		//		watchDog.start();

		//mCountDownLatch.countDown();
		//		try
		//		{
		//			mCountDownLatch.await();
		//		}
		//		catch(Exception e)
		//		{
		//			e.printStackTrace();
		//		}

		//利用系统空闲发送消息
		//		MessageQueue.IdleHandler handler=new MessageQueue.IdleHandler()
		//		{
		//			@Override
		//			public boolean queueIdle()
		//			{
		//				return false;
		//			}
		//		};
		//		Looper.myQueue().addIdleHandler(handler);

		//Debug.dumpHprofData("app");


		//SysTrace
		//Trace.beginSection("trace");
		//Trace.endSection();

		//TraceView
		//Debug.startMethodTracing("");
		//Debug.stopMethodTracing();

		TopActivity.init(this);

		//		Thread thread=new Thread(()->
		//		{
		//			for(int i=0;i<200;i++)
		//			{
		//				StackTraceElement[] elements=Looper.getMainLooper().getThread().getStackTrace();
		//				StackTraceElement[] traces=Arrays.copyOf(elements,elements.length);
		//				StackTracker.add(traces,System.currentTimeMillis());
		//				SystemClock.sleep(2);
		//			}
		//			StackTracker.print();
		//		});
		//		thread.start();
	}

	@Override
	public String getPackageName()
	{
		StackTraceElement[] stacks=Thread.currentThread().getStackTrace();
		boolean flag=false;
		for(StackTraceElement stack : stacks)
		{
			if(isNeedHandle(stack.toString()))
			{
				flag=true;
			}
		}

		if(flag)
		{
			return "xxx.xxx.xxx";
		}

		//		Exception ex=new Exception("");
		//		stacks=ex.getStackTrace();
		//		for(StackTraceElement stack:stacks)
		//		{
		//			System.err.println(stack);
		//		}
		return super.getPackageName();
	}

	/**
	 * 是否需要处理
	 *
	 * @param methodName 方法名
	 * @return 是否需要处理
	 */
	private boolean isNeedHandle(String methodName)
	{
		boolean b=false;
		b=b||methodName.contains("com.xxx.xxx");
		return b;
	}

	/**
	 * 代码混淆时在BaseActivity中clazz.newInstance()会出现<br/>
	 * java.lang.InstantiationException: <br/>
	 * java.lang.Class<com.b.a.a.b.b> has no zero argument constructor<br/>
	 * 原因未知,但是在newInstance()之前执行new XX()操作可以解决这个问题
	 */
	private void fixProguard()
	{
		BaseAgent.putAgent(PreviewAgent.class,new PreviewAgent());
	}
}