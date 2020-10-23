package commons.widget.utils;

import android.os.Handler;
import android.os.HandlerThread;

public class EasyShow
{
	private static final Handler HANDLER;

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
}