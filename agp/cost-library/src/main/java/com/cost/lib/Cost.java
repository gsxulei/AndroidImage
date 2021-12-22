package com.cost.lib;

import android.util.Log;

public class Cost
{
	public static void cost(long time)
	{
		if(time>0)
		{
			Exception exception=new Exception();
			StackTraceElement element=exception.getStackTrace()[1];
			Log.e("Cost",element.getClassName()+"."+element.getMethodName()+"() cost "+time+"ms");
		}
	}
}