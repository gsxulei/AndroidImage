package com.x62.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * 状态栏处理
 */
public class SystemBarCompat
{
	public static void tint(Activity activity)
	{
		Window window=activity.getWindow();
		ViewGroup content=(ViewGroup)window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
		//Window.ID_ANDROID_CONTENT
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|WindowManager.LayoutParams
					.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View
					.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

			//content.setFitsSystemWindows(true);
			content.getChildAt(0).setFitsSystemWindows(true);

			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
			//window.setNavigationBarColor(Color.TRANSPARENT);
		}
		else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			WindowManager.LayoutParams lp=window.getAttributes();
			lp.flags|=WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;//|WindowManager.LayoutParams
			// .FLAG_TRANSLUCENT_NAVIGATION;

			//content.setFitsSystemWindows(true);
			content.getChildAt(0).setFitsSystemWindows(true);
		}

		//默认API 最低19
		//		if(Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN_MR2)
		//		{
		//			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		//			ViewGroup contentView=(ViewGroup)window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
		//			contentView.getChildAt(0).setFitsSystemWindows(false);
		//		}
	}


	public static void fullscreen(Activity context)
	{
		Window window=context.getWindow();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|WindowManager.LayoutParams
					.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View
					.SYSTEM_UI_FLAG_LAYOUT_STABLE);

			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
		}
	}

	public static void fullscreen(Activity context,View content,boolean fitSystem)
	{
		Window window=context.getWindow();

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|WindowManager.LayoutParams
					.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View
					.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

			content.setFitsSystemWindows(fitSystem);

			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
			window.setNavigationBarColor(Color.TRANSPARENT);
		}
		else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			WindowManager.LayoutParams lp=window.getAttributes();
			lp.flags|=WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|WindowManager.LayoutParams
					.FLAG_TRANSLUCENT_NAVIGATION;

			content.setFitsSystemWindows(fitSystem);
		}
	}

	/**
	 * 图片预览模式：无状态栏、无actionBar、底部导航键背景透明
	 *
	 * @param activity
	 */
	public static void imagePreviewMode(Activity activity)
	{
		fullScreen(activity);
		hideNavigationBar(activity);
	}

	public static void fullScreen(Activity activity)
	{
		Window window=activity.getWindow();
		int flags=WindowManager.LayoutParams.FLAG_FULLSCREEN;
		int mask=flags;
		window.setFlags(flags,mask);//设置全屏
	}

	/**
	 * 隐藏底部虚拟按键
	 *
	 * @param activity
	 */
	public static void hideNavigationBar(Activity activity)
	{
		Window window=activity.getWindow();
		window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View
				.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|WindowManager.LayoutParams
				.FLAG_TRANSLUCENT_NAVIGATION);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			window.setNavigationBarColor(Color.TRANSPARENT);
		}
	}
}