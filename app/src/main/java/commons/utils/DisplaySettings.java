package commons.utils;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

/**
 * 屏幕适配
 */
public class DisplaySettings implements Application.ActivityLifecycleCallbacks
{
	public static void setDpi(Application app)
	{
		setDpi(app.getResources());

		//Android7.1之后需要对Activity的Resources也做设置
		app.registerActivityLifecycleCallbacks(new DisplaySettings());
	}

	private static void setDpi(Resources res)
	{
		float wDpi=360;
		DisplayMetrics dm=res.getDisplayMetrics();
		float density=dm.widthPixels/wDpi;
		int densityDpi=(int)(density*DisplayMetrics.DENSITY_DEFAULT);
		dm.density=density;
		dm.densityDpi=densityDpi;
	}

	@Override
	public void onActivityCreated(Activity activity,Bundle savedInstanceState)
	{
		setDpi(activity.getResources());
	}

	@Override
	public void onActivityStarted(Activity activity)
	{
	}

	@Override
	public void onActivityResumed(Activity activity)
	{
	}

	@Override
	public void onActivityPaused(Activity activity)
	{
	}

	@Override
	public void onActivityStopped(Activity activity)
	{
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity,Bundle outState)
	{
	}

	@Override
	public void onActivityDestroyed(Activity activity)
	{
	}
}