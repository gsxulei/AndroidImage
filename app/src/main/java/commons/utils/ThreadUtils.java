package commons.utils;

import android.os.Looper;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ThreadUtils
{
	public static String getMainStackTrace()
	{
		return getStackTrace(Looper.getMainLooper().getThread());
	}

	public static String getStackTrace(Thread thread)
	{
		Throwable throwable=new Throwable();
		throwable.setStackTrace(thread.getStackTrace());
		return getStackTrace(throwable);
	}

	public static String getStackTrace(Throwable throwable)
	{
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		throwable.printStackTrace(pw);

		return sw.toString();
	}
}