package com.x62;

import android.app.Application;

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
	}
}