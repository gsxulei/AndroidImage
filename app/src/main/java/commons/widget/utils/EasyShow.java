package commons.widget.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class EasyShow implements Runnable
{
	private static final Handler HANDLER;
	private static final Handler MAIN_HANDLER=new Handler(Looper.getMainLooper());
	private static final Thread.UncaughtExceptionHandler defCatcher;
	private static final boolean isSysCatcher;

	static
	{
		defCatcher=Thread.getDefaultUncaughtExceptionHandler();

		//com.android.internal.os.RuntimeInit$UncaughtHandler
		//com.android.internal.os.RuntimeInit$KillApplicationHandler 8.0+
		isSysCatcher=(defCatcher==null||defCatcher.getClass().getName().startsWith("com.android.internal.os"));
	}

	static
	{
		HandlerThread thread=new HandlerThread("EasyShow");
		thread.start();
		HANDLER=new Handler(thread.getLooper());
		HANDLER.postAtFrontOfQueue(new EasyShow());
	}

	public static void post(Runnable r)
	{
		HANDLER.post(r);
	}

	public static void postMain(Runnable r)
	{
		MAIN_HANDLER.post(r);
	}

	@SuppressWarnings("InfiniteLoopStatement")
	@Override
	public void run()
	{
		for(;;)
		{
			try
			{
				Looper.loop();
			}
			catch(Exception e)
			{
				if(isSysCatcher)
				{
					e.printStackTrace();
				}
				else
				{
					defCatcher.uncaughtException(Thread.currentThread(),e);
				}
			}
		}
	}
}