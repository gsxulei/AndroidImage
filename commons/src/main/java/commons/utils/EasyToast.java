package commons.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class EasyToast
{
	private static Context mCtx;

	public static void init(Application app)
	{
		app.registerActivityLifecycleCallbacks(new Callbacks()
		{
			@Override
			public void onActivityCreated(Activity activity,Bundle savedInstanceState)
			{
				super.onActivityCreated(activity,savedInstanceState);
				mCtx=activity;
			}

			@Override
			public void onActivityDestroyed(Activity activity)
			{
				super.onActivityDestroyed(activity);
				mCtx=null;
			}
		});
	}

	public static void show(String msg)
	{
		if(mCtx!=null)
		{
			return;
		}
		Toast toast=Toast.makeText(mCtx,msg,Toast.LENGTH_SHORT);
		toast.show();
	}

	private static class Callbacks implements Application.ActivityLifecycleCallbacks
	{
		@Override
		public void onActivityCreated(Activity activity,Bundle savedInstanceState)
		{
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
}