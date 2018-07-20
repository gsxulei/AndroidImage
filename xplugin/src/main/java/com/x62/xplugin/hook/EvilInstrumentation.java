package com.x62.xplugin.hook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.x62.xplugin.obj.Plugin;
import com.x62.xplugin.utils.PluginManager;

import android.app.Activity;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources.Theme;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

public class EvilInstrumentation extends Instrumentation
{
	// ActivityThread中原始的对象,保存起来
	private Instrumentation mBase;
	private Context hostContext;

	/**
	 * 宿主中注册的Activity
	 */
	private Class<?> legalActivity;

	public EvilInstrumentation(Instrumentation base,Context context)
	{
		mBase=base;
		hostContext=context;

		PackageManager pm=context.getPackageManager();
		try
		{
			PackageInfo packageInfo=pm.getPackageInfo(context.getPackageName(),PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activities=packageInfo.activities;
			String activity="";
			if(activities!=null&&activities.length>0)
			{
				activity=activities[0].name;
			}
			legalActivity=Class.forName(activity);
			// System.err.println("context.getPackageName()->"+context.getPackageName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public ActivityResult execStartActivity(Context who,IBinder contextThread,IBinder token,Activity target,
			Intent intent,int requestCode,Bundle options)
	{
		System.err.println("--startActivity for 23--");
		System.err.println("intent->"+intent.getComponent().getClassName());
		System.err.println("Context->"+who);
		System.err.println("Context->who.getPackageName()->"+who.getPackageName());
		System.err.println("Context->ClassLoader->"+who.getClass().getClassLoader());

		replaceActivity4Check(who,intent);

		// 开始调用原始的方法, 调不调用随你,但是不调用的话, 所有的startActivity都失效了.
		// 由于这个方法是隐藏的,因此需要使用反射调用;首先找到这个方法
		try
		{
			Method execStartActivity=Instrumentation.class.getDeclaredMethod("execStartActivity",Context.class,
					IBinder.class,IBinder.class,Activity.class,Intent.class,int.class,Bundle.class);
			execStartActivity.setAccessible(true);
			return (ActivityResult)execStartActivity.invoke(mBase,who,contextThread,token,target,intent,requestCode,
					options);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			// 某该死的rom修改了 需要手动适配
			throw new RuntimeException("do not support!!! pls adapt it");
		}
	}

	/**
	 * 15以下版本
	 * 
	 * @param who
	 * @param contextThread
	 * @param token
	 * @param target
	 * @param intent
	 * @param requestCode
	 * @return
	 */
	public ActivityResult execStartActivity(Context who,IBinder contextThread,IBinder token,Activity target,
			Intent intent,int requestCode)
	{
		System.err.println("--startActivity for 15--");
		System.err.println("intent->"+intent.getClass().getClassLoader());
		System.err.println("Context->"+who);
		System.err.println("Context->ClassLoader->"+who.getClass().getClassLoader());

		/**
		 * 15以下版本中使用Context.startActivity会多次进入此方法,原因未知<br/>
		 * 或许是这种方式启动的Activity没有 Activity栈
		 */

		replaceActivity4Check(who,intent);

		// 开始调用原始的方法, 调不调用随你,但是不调用的话, 所有的startActivity都失效了.
		// 由于这个方法是隐藏的,因此需要使用反射调用;首先找到这个方法
		try
		{
			Method execStartActivity=Instrumentation.class.getDeclaredMethod("execStartActivity",Context.class,
					IBinder.class,IBinder.class,Activity.class,Intent.class,int.class);
			execStartActivity.setAccessible(true);
			return (ActivityResult)execStartActivity.invoke(mBase,who,contextThread,token,target,intent,requestCode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			// 某该死的rom修改了 需要手动适配
			throw new RuntimeException("do not support!!! pls adapt it");
		}
	}

	public ActivityResult execStartActivity(Context who,IBinder contextThread,IBinder token,Fragment target,
			Intent intent,int requestCode)
	{
		replaceActivity4Check(who,intent);

		try
		{
			Method execStartActivity=Instrumentation.class.getDeclaredMethod("execStartActivity",Context.class,
					IBinder.class,IBinder.class,Fragment.class,Intent.class,int.class);
			execStartActivity.setAccessible(true);
			return (ActivityResult)execStartActivity.invoke(mBase,who,contextThread,token,target,intent,requestCode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			// 某该死的rom修改了 需要手动适配
			throw new RuntimeException("do not support!!! pls adapt it");
		}
	}

	@Override
	public Activity newActivity(ClassLoader cl,String className,
			Intent intent) throws InstantiationException,IllegalAccessException,ClassNotFoundException
	{
		System.err.println("--newActivity--");

		String pluginName=intent.getStringExtra("plugin");
		Activity activity;
		if(!TextUtils.isEmpty(pluginName))
		{
			String real=intent.getStringExtra("real");
			if(!TextUtils.isEmpty(real))
			{
				className=real;
				intent.removeExtra("real");
			}
			Plugin plugin=PluginManager.getInstance().get(pluginName);
			activity=(Activity)plugin.loader.loadClass(className).newInstance();
			//new
			//activity.atta
		}
		else
		{
			activity=(Activity)cl.loadClass(className).newInstance();
		}
		return activity;
	}

	@Override
	public void callActivityOnCreate(Activity activity,Bundle icicle)
	{
		String pluginName=activity.getIntent().getStringExtra("plugin");
		System.err.println("--callActivityOnCreate--"+pluginName);
		if(!TextUtils.isEmpty(pluginName))
		{
			Plugin plugin=PluginManager.getInstance().get(pluginName);
			activity.getIntent().removeExtra("plugin");
			try
			{
				/**
				 * SDK>16时
				 * getResources()方法在Activity的父类(ContextThemeWrapper)中被重写了
				 * 
				 */
				int SDK=Build.VERSION.SDK_INT;
				if(SDK<=16)
				{
					Field mBaseField=Activity.class.getSuperclass().getSuperclass().getDeclaredField("mBase");
					mBaseField.setAccessible(true);
					Object obj=mBaseField.get(activity);

					Class<?> clazz=Class.forName("android.app.ContextImpl");
					Field mResources=clazz.getDeclaredField("mResources");
					mResources.setAccessible(true);
					mResources.set(obj,plugin.resources);
				}
				else if(SDK>16)
				{
					Field mResources=Activity.class.getSuperclass().getDeclaredField("mResources");
					mResources.setAccessible(true);
					mResources.set(activity,plugin.resources);
				}

				//Theme不能用activity.setTheme(resid);
				Theme theme=plugin.resources.newTheme();
				theme.applyStyle(plugin.themeResource,true);
				// theme.setTo(activity.getTheme());

				Class<?> clazz=Activity.class.getSuperclass();
				Field mTheme=clazz.getDeclaredField("mTheme");
				mTheme.setAccessible(true);
				mTheme.set(activity,theme);
				// activity.setTheme(resid);
				// activity.getApplication().setTheme(plugin.themeResource);
				

				// System.err.println("callActivityOnCreate->"+activity.getResources());
				// System.err.println("activity.getTheme()->"+activity.getTheme());
				// System.err.println("宿主->theme->"+theme);

				// 标题栏图标及文字
				ActivityInfo ai=plugin.aisMap.get(activity.getClass().getName());
				Class<Activity> clazzActivity=Activity.class;
				Field field=clazzActivity.getDeclaredField("mActivityInfo");
				field.setAccessible(true);
				field.set(activity,ai);

				// CharSequence
				// title=ai.loadLabel(activity.getPackageManager());
				// activity.gett
				// Field mTitle=clazzActivity.getDeclaredField("mTitle");
				// mTitle.setAccessible(true);
				// mTitle.set(activity,title);
				// activity.setTitle(title);
				int titleId=ai.labelRes;
				//System.err.println("titleId->"+Integer.toHexString(titleId));
				if(titleId==0||titleId==-1)
				{
					titleId=plugin.ai.labelRes;
				}
				activity.setTitle(titleId);

				// 插件自定义Application
				System.err.println("plugin.ai.className->"+plugin.ai.className);
				if(!TextUtils.isEmpty(plugin.ai.className))
				{
					Field mApplication=clazzActivity.getDeclaredField("mApplication");
					mApplication.setAccessible(true);
					mApplication.set(activity,plugin.app);
				}

				/**
				 * 替换掉内部存储的根路径
				 */
				// 拿到mBase
				Field mBaseField=Activity.class.getSuperclass().getSuperclass().getDeclaredField("mBase");
				mBaseField.setAccessible(true);
				Object obj=mBaseField.get(activity);

				// 拿到LoadedApk
				Class<?> contextImpl=Class.forName("android.app.ContextImpl");
				Field mPackageInfo=contextImpl.getDeclaredField("mPackageInfo");
				mPackageInfo.setAccessible(true);
				// 不能直接替换,AMN不仅要检查被启动的Activity,而且要检查启动Activity
				mPackageInfo.set(obj,plugin.loadedApk);
				// Object mPackageInfoObj=mPackageInfo.get(obj);
				// System.err.println("plugin.loadedApk->"+plugin.loadedApk.getDataDir());
				//
				// 替换掉LoadedApk的mDataDirFile属性
				// Field
				// mDataDirFile=LoadedApk.class.getDeclaredField("mDataDirFile");
				// mDataDirFile.setAccessible(true);
				// mDataDirFile.set(mPackageInfoObj,plugin.dataDirFile);

				// 替换掉Activity的ClassLoader,用于在XML中加载自定义View,只能在ActivityonCreate之前调用
				// Field
				// mClassLoader=LoadedApk.class.getDeclaredField("mClassLoader");
				// mClassLoader.setAccessible(true);
				// mClassLoader.set(plugin.loadedApk,plugin.loader);

				//activity.setRequestedOrientation(ai.screenOrientation);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		super.callActivityOnCreate(activity,icicle);
	}

	private void replaceActivity4Check(Context who,Intent intent)
	{
		ClassLoader classLoader=who.getClass().getClassLoader();
		PluginManager pm=PluginManager.getInstance();
		Plugin p=pm.get(classLoader);

		String plugin=intent.getStringExtra("plugin");
		if((!TextUtils.isEmpty(plugin))||(p!=null))
		{
			String className=intent.getComponent().getClassName();
			String real=intent.getStringExtra("real");
			if(TextUtils.isEmpty(real))
			{
				intent.putExtra("real",className);
				intent.setClass(hostContext,legalActivity);
			}
			if(TextUtils.isEmpty(plugin))
			{
				intent.putExtra("plugin",p.packageName);
			}
		}
	}
}