package commons.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * 系统栏(状态栏、导航栏)处理
 */
public class SysBarUtils
{
	/**
	 * 状态栏着色
	 *
	 * @param activity
	 * @param color
	 */
	public static void statusBarTint(Activity activity,int color)
	{
		Window window=activity.getWindow();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			window.setStatusBarColor(color);
		}
		else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			//android.R.id.content
			ViewGroup content=(ViewGroup)window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
			content.getChildAt(0).setFitsSystemWindows(true);
			View statusBarView=new View(activity);
			ViewGroup.LayoutParams lp=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					getStatusBarHeight(activity));
			statusBarView.setBackgroundColor(color);
			content.addView(statusBarView,lp);

			//一定要加这一句
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

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

	private static int getStatusBarHeight(Activity activity)
	{
		int result=0;
		//获取状态栏高度的资源id
		int resourceId=activity.getResources().getIdentifier("status_bar_height","dimen","android");
		if(resourceId>0)
		{
			result=activity.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
}