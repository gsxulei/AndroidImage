package commons.widget.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class EasyShow
{
	private static final Handler HANDLER;
	private static final Handler MAIN_HANDLER=new Handler(Looper.getMainLooper());

	static
	{
		HandlerThread thread=new HandlerThread("EasyShow");
		thread.start();
		HANDLER=new Handler(thread.getLooper());
	}

	public static void post(Runnable r)
	{
		HANDLER.post(r);
	}

	public static void postMain(Runnable r)
	{
		MAIN_HANDLER.post(r);
	}
}