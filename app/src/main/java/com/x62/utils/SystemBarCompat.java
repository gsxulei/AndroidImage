package com.x62.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * 状态栏处理<br/>
 * 对system bar着色，有两种做法：<br/>
 * 1、使用activity的content view覆盖到system bar，然后通过fitSystemWindows()来控制content view与system bar的位置<br/>
 * 2、直接调用着色方法对system bar进行着色<br/>
 * <p>
 * 方法1的好处是不需要使用SystemBarTint对android 4.4-android 5.0的手机进行兼容。<br/>
 * 而且content view的背景可以不只是颜色，可以是视图。<br/>
 * 坏处是必须要设置content view的背景色，一般content view设置背景色，然后其子视图也设置背景色，就会导致过度绘制。<br/>
 * <br/>
 * 方法2的好处是可以只对system bar设置颜色，不会过度绘制。<br/>
 * 坏处是需要SystemBatTint，而且，它只能着色。当碰到需求是图片或者渐变色之类的<br/>
 * 时候，只能使用方法1。<br/>
 * <p>
 * 如果不是需求问题，最好使用方法2，避免过度绘制；否则选择方法1。<br/>
 * <p>
 * 注意事项：<br/>
 * 程序运行过程中修改content背景，可能导致padding变化，从而使着色失效。需要在更换背景前，<br/>
 * 保存padding，更换以后重新设置padding。<br/>
 */
public class SystemBarCompat
{
	public static void tint(Activity activity)
	{
		tint(activity,Color.TRANSPARENT);
	}

	/**
	 * content - layout.xml的root视图
	 * 仅仅只是使用content背景作为system bar背景，不需要SystemBarTintManager适配4.4。
	 * 对应方法1
	 */
	public static void tint(Activity activity,int color)
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
			window.setStatusBarColor(color);
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

	//	public static void tint(Activity context,View content)
	//	{
	//		Window window=context.getWindow();
	//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
	//		{
	//			window=context.getWindow();
	//			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|WindowManager.LayoutParams
	//					.FLAG_TRANSLUCENT_NAVIGATION);
	//			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View
	//					.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
	//
	//			content.setFitsSystemWindows(true);
	//
	//			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
	//			window.setStatusBarColor(Color.TRANSPARENT);
	//			//            window.setNavigationBarColor(Color.TRANSPARENT);
	//		}
	//		else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
	//		{
	//			WindowManager.LayoutParams lp=window.getAttributes();
	//			lp.flags|=WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
	//			//                   |WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
	//
	//			content.setFitsSystemWindows(true);
	//		}
	//	}

	/**
	 * 对应方法2
	 */
	//	public static void tint(Activity context,View content,int color)
	//	{
	//		Window window=context.getWindow();
	//
	//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
	//		{
	//			window.setStatusBarColor(color);
	//			//            window.setNavigationBarColor(color);
	//		}
	//		else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
	//		{
	//			WindowManager.LayoutParams lp=window.getAttributes();
	//			lp.flags|=WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
	//			//                   |WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
	//			content.setFitsSystemWindows(true);
	//
	//			SystemBarTintManager tintManager=new SystemBarTintManager(context);
	//			tintManager.setStatusBarTintEnabled(true);
	//			tintManager.setStatusBarTintColor(color);
	//			//           tintManager.setNavigationBarTintEnabled(true);
	//			//           tintManager.setNavigationBarTintColor(color);
	//		}
	//	}
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
		//window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		//window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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

	//	public static void hideStatusBar(Activity activity)
	//	{
	//		Window window=activity.getWindow();
	//		int visibility=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
	//		window.getDecorView().setSystemUiVisibility(visibility);
	//	}
}