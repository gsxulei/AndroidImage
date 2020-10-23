package commons.widget.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * 界面最上层Activity
 */
public class TopActivity implements Application.ActivityLifecycleCallbacks
{
	private static WeakReference<Activity> mAct;

	/**
	 * 初始化
	 *
	 * @param app Application
	 */
	public static void init(Application app)
	{
		app.registerActivityLifecycleCallbacks(new TopActivity());
	}

	private static void set(Activity activity)
	{
		mAct=new WeakReference<>(activity);
	}

	/**
	 * 获取界面最上层Activity
	 *
	 * @return 界面最上层Activity
	 */
	public static Activity get()
	{
		return mAct==null?null:mAct.get();
	}

	@Override
	public void onActivityCreated(Activity activity,Bundle savedInstanceState)
	{
		set(activity);
	}

	@Override
	public void onActivityStarted(Activity activity)
	{
		set(activity);
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