package com.x62;

import android.app.Application;

import com.x62.commons.base.ImageLoaderWrapper;
import com.x62.image.R;
import com.x62.utils.CrashHandler;
import com.x62.utils.PathUtils;
import com.x62.commons.utils.ScreenUtils;

/**
 * Created by GSXL on 2018-05-05.
 */

public class AndroidApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();

		//崩溃处理初始化
		CrashHandler.getInstance().init(this,PathUtils.getCrashPath());

		//设置屏幕宽度dpi
		ScreenUtils.getInstance().setDpi();

		ImageLoaderWrapper.setDefault(R.mipmap.ic_launcher);
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
	 * @return
	 */
	private boolean isNeedHandle(String methodName)
	{
		boolean b=false;
		b=b||methodName.contains("com.xxx.xxx");
		return b;
	}
}