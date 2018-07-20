package com.x62.xplugin.api;

import java.lang.reflect.Constructor;

import android.app.ActivityThread;
import android.app.LoadedApk;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.os.Build;

public class APISupport
{
	private static final int SDK=Build.VERSION.SDK_INT;

	public static LoadedApk getLoadedApk(ActivityThread activityThread,ApplicationInfo aInfo,ClassLoader baseLoader)
	{
		LoadedApk loadedApk=null;
		try
		{
			CompatibilityInfo compatInfo=CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
			boolean securityViolation=false;
			boolean includeCode=true;
			boolean registerPackage=false;

			Constructor<LoadedApk> constructor;
			Class<LoadedApk> clazz=LoadedApk.class;

			if(SDK<=19)// Android 4.4.2
			{
				constructor=clazz.getDeclaredConstructor(ActivityThread.class,ApplicationInfo.class,
						CompatibilityInfo.class,ActivityThread.class,ClassLoader.class,boolean.class,boolean.class);
				loadedApk=constructor.newInstance(activityThread,aInfo,compatInfo,activityThread,baseLoader,
						securityViolation,includeCode);
			}
			else if(SDK==20)// Android 4.4W.2
			{
				constructor=clazz.getDeclaredConstructor(ActivityThread.class,ApplicationInfo.class,
						CompatibilityInfo.class,ClassLoader.class,boolean.class,boolean.class);
				loadedApk=constructor.newInstance(activityThread,aInfo,compatInfo,baseLoader,securityViolation,
						includeCode);
			}
			else if(SDK>=21)// Android 5.0
			{
				// LoadedApk loadedApk=new
				// LoadedApk(mActivityThread,plugin.ai,CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO,
				// plugin.loader,false,true,false);
				constructor=clazz.getDeclaredConstructor(ActivityThread.class,ApplicationInfo.class,
						CompatibilityInfo.class,ClassLoader.class,boolean.class,boolean.class,boolean.class);
				loadedApk=constructor.newInstance(activityThread,aInfo,compatInfo,baseLoader,securityViolation,
						includeCode,registerPackage);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return loadedApk;
	}
}