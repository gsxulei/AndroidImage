package commons.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

import commons.halt.HaltException;

/**
 * 监控线程崩溃并接管主线程Looper
 */
public class CrashMonitor implements Runnable,Thread.UncaughtExceptionHandler
{
	private static final List<OnLoopListener> lopListeners=new ArrayList<>();
	private static final List<OnCrashListener> crashListeners=new ArrayList<>();

	/**
	 * 初始化
	 */
	public static void init()
	{
		CrashMonitor crash=new CrashMonitor();
		Handler handler=new Handler(Looper.getMainLooper());
		handler.postAtFrontOfQueue(crash);

		Thread.setDefaultUncaughtExceptionHandler(crash);
	}

	@SuppressWarnings("InfiniteLoopStatement")
	@Override
	public void run()
	{
		for(;;)
		{
			try
			{
				for(OnLoopListener listener : lopListeners)
				{
					listener.onStartLoop();
				}
				Looper.loop();
			}
			catch(Exception e)
			{
				onCrash(Thread.currentThread(),e);
			}
		}
	}

	@Override
	public void uncaughtException(Thread thread,Throwable ex)
	{
		onCrash(thread,ex);
	}

	public static void onCrash(Thread thread,Throwable ex)
	{
		Throwable cause=ex;
		while(cause.getCause()!=null)
		{
			cause=cause.getCause();
		}

		if(cause instanceof HaltException)
		{
			((HaltException)cause).onHalt();
		}
		else
		{
			for(OnCrashListener listener : crashListeners)
			{
				listener.onCrash(thread,cause);
			}
		}
	}

	public static void addOnLoopListener(OnLoopListener listener)
	{
		if(listener!=null)
		{
			lopListeners.add(listener);
		}
	}

	public static void addOnCrashListener(OnCrashListener listener)
	{
		if(listener!=null)
		{
			crashListeners.add(listener);
		}
	}

	public interface OnLoopListener
	{
		void onStartLoop();
	}

	public interface OnCrashListener
	{
		void onCrash(Thread thread,Throwable ex);
	}
}