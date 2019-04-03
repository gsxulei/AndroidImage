package commons.utils;

import android.util.Log;

/**
 * 日志
 */
public class Logger
{
	private static final String TAG="xulei";

	public static void e(String... args)
	{
		StringBuilder msg=new StringBuilder();
		if(args!=null&&args.length>0)
		{
			for(String s : args)
			{
				msg.append(s);
			}
		}

		String text=" \n";
		text+="╔"+getLine("═")+"\n";
		text+="║\t"+getMethod()+"\n";
		text+="║"+getLine("─")+"\n";
		text+="║\t"+msg.toString()+"\n";
		text+="╚"+getLine("═");
		Log.e(TAG,text);
	}

	private static String getLine(String type)
	{
		StringBuilder line=new StringBuilder();
		for(int i=0;i<80;i++)
		{
			line.append(type);
		}
		return line.toString();
	}

	private static String getMethod()
	{
		StackTraceElement[] stacks=Thread.currentThread().getStackTrace();
		//		StringBuilder name=new StringBuilder();
		//		for(StackTraceElement stack : stacks)
		//		{
		//			name.append(stack.toString());
		//			name.append("\n");
		//		}
		if(stacks.length>5)
		{
			return stacks[4].toString();
		}
		return "";
	}
}