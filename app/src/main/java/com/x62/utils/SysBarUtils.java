package com.x62.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;

/**
 * 系统栏(状态栏、导航栏)处理
 */
public class SysBarUtils
{
	/**
	 * 系统栏(状态栏、导航栏)悬浮
	 *
	 * @param activity
	 */
	public static void sysBarFloat(Activity activity)
	{
		Window window=activity.getWindow();
		int visibility=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		visibility|=View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
		visibility|=View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
		window.getDecorView().setSystemUiVisibility(visibility);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			window.setStatusBarColor(Color.TRANSPARENT);
			window.setNavigationBarColor(Color.TRANSPARENT);
		}
	}

	/**
	 * 状态栏悬浮
	 *
	 * @param activity
	 */
	public static void statusBarFloat(Activity activity)
	{
		Window window=activity.getWindow();
		int visibility=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		visibility|=View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
		window.getDecorView().setSystemUiVisibility(visibility);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			window.setStatusBarColor(Color.TRANSPARENT);
		}
	}

	/**
	 * 导航栏悬浮
	 *
	 * @param activity
	 */
	public static void navigationBarFloat(Activity activity)
	{
		Window window=activity.getWindow();
		int visibility=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		visibility|=View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
		window.getDecorView().setSystemUiVisibility(visibility);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			window.setNavigationBarColor(Color.TRANSPARENT);
		}
	}
}