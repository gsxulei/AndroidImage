package com.x62.xplugin.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityThread;
import android.app.Instrumentation;
import android.app.LoadedApk;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import commons.utils.Cache;
import com.x62.xplugin.api.APISupport;
import com.x62.xplugin.obj.Plugin;

import dalvik.system.DexClassLoader;

/**
 * 插件管理
 * 
 */
public class PluginManager
{
	private Cache cache=Cache.getInstance();
	private Map<String,Plugin> plugins=new HashMap<String,Plugin>();

	private PluginManager()
	{
	}

	private static class Loader
	{
		public static final PluginManager INSTANCE=new PluginManager();
	}

	public static PluginManager getInstance()
	{
		return Loader.INSTANCE;
	}

	public void put(String name,Plugin plugin)
	{
		plugins.put(name,plugin);
	}

	public Plugin get(String name)
	{
		return plugins.get(name);
	}

	public Plugin get(ClassLoader classLoader)
	{
		Plugin plugin=null;
		for(Map.Entry<String,Plugin> entry:plugins.entrySet())
		{
			Plugin p=entry.getValue();
			if(p.loader==classLoader)
			{
				plugin=p;
				break;
			}
		}
		return plugin;
	}

	public boolean isInstall(Context context,String path)
	{
		PackageInfo pi=context.getPackageManager().getPackageArchiveInfo(path,PackageManager.GET_ACTIVITIES);
		return cache.read(pi.packageName+"-install",false);
	}

	public String install(Context context,String path)
	{
		PackageInfo newPi=context.getPackageManager().getPackageArchiveInfo(path,PackageManager.GET_ACTIVITIES);

		// TODO 校验APP版本等信息(如果已安装,则版本号不能小于已安装)
		// checkApp();
		boolean isInstall=cache.read(newPi.packageName+"-install",false);
		if(isInstall)
		{
		}

		File apk=getApkFile(newPi.packageName,context);
		Utils.copy(path,apk.getAbsolutePath());

		String dexOutputPath=getDexFile(newPi.packageName,context).getAbsolutePath();
		ClassLoader parent=ClassLoader.getSystemClassLoader();
		new DexClassLoader(apk.getAbsolutePath(),dexOutputPath,null,parent);
		cache.save(newPi.packageName+"-install",true);
		return newPi.packageName;
	}

	public Plugin loadPlugin(Context context,String packageName)
	{
		Plugin plugin=new Plugin();
		plugin.apkPath=getApkFile(packageName,context).getAbsolutePath();

		PackageInfo pi=context.getPackageManager().getPackageArchiveInfo(plugin.apkPath,0xFFFFFFFF);// PackageManager.GET_ACTIVITIES
		plugin.packageName=pi.packageName;
		ActivityInfo[] as=pi.activities;

		plugin.ai=pi.applicationInfo;
		plugin.themeResource=pi.applicationInfo.theme;
		plugin.ai.sourceDir=plugin.apkPath;
		plugin.ai.publicSourceDir=plugin.apkPath;

		System.err.println("sourceDir->"+plugin.ai.sourceDir);
		System.err.println("publicSourceDir->"+plugin.ai.publicSourceDir);

		plugin.dataDirFile=getPluginDataDirFile(plugin.packageName,context);
		plugin.ai.dataDir=plugin.dataDirFile.getAbsolutePath();

		// plugin.assetManager=createAssetManager(context,plugin.apkPath);
		// Resources res=context.getResources();
		// Resources resources=new
		// Resources(plugin.assetManager,res.getDisplayMetrics(),res.getConfiguration());
		// plugin.resources=resources;

		// new LoadedApk
		ActivityThread mActivityThread=ActivityThread.currentActivityThread();

		LoadedApk loadedApk=APISupport.getLoadedApk(mActivityThread,plugin.ai,plugin.loader);
		// LoadedApk不同版本API构造方法不同
		// 此方法API>=21
		// LoadedApk loadedApk=new
		// LoadedApk(mActivityThread,plugin.ai,CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO,
		// plugin.loader,false,true,false);
		// mActivityThread.getPackageInfoNoCheck(plugin.ai,CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO);

		plugin.loadedApk=loadedApk;
		System.err.println("loadedApk->getResDir->"+loadedApk.getResDir());
		System.err.println("loadedApk->getResources->"+loadedApk.getResources(mActivityThread));
		System.err.println("plugin.resources->"+plugin.resources);

		try
		{
			// String resDir=loadedApk.getResDir();
			// String[] splitResDirs=loadedApk.getSplitResDirs();
			// String[] overlayDirs=loadedApk.getOverlayDirs();
			// String[]
			// libDirs=loadedApk.getApplicationInfo().sharedLibraryFiles;
			// int displayId=Display.DEFAULT_DISPLAY;
			// Configuration overrideConfiguration=null;
			// CompatibilityInfo compatInfo=loadedApk.getCompatibilityInfo();
			// IBinder token=null;
			//
			// // 注意API版本不同getTopLevelResources差异
			// Resources
			// resources=ResourcesManager.getInstance().getTopLevelResources(resDir,splitResDirs,overlayDirs,
			// libDirs,displayId,overrideConfiguration,compatInfo,token);
			// System.err.println("getTopLevelResources->"+resources);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		plugin.resources=loadedApk.getResources(mActivityThread);
		plugin.assetManager=plugin.resources.getAssets();

		// try
		// {
		// // Field
		// // mDataDirFile=LoadedApk.class.getDeclaredField("mDataDirFile");
		// // mDataDirFile.setAccessible(true);
		// // mDataDirFile.set(loadedApk,plugin.dataDirFile);
		//
		// // Field mResDir=LoadedApk.class.getDeclaredField("mResDir");
		// // mResDir.setAccessible(true);
		// // mResDir.set(loadedApk,plugin.ai.sourceDir);
		//
		// // mClassLoader
		// // Field
		// // mClassLoader=LoadedApk.class.getDeclaredField("mClassLoader");
		// // mClassLoader.setAccessible(true);
		// // mClassLoader.set(loadedApk,plugin.loader);
		// }
		// catch(Exception e)
		// {
		// e.printStackTrace();
		// }

		// System.err.println("loadedApk->"+loadedApk.getPackageName());
		// System.err.println("loadedApk->"+loadedApk.getResDir());
		// System.err.println("loadedApk->"+loadedApk.getAppDir());

		ManifestParser manifestParser=new ManifestParser(plugin.assetManager);
		plugin.launchActivity=manifestParser.launchActivity.get(0);

		if(as!=null&&as.length>0)
		{
			// 如果插件APP没有启动Activity,则把第一个Activity作为启动
			if(TextUtils.isEmpty(plugin.launchActivity))
			{
				plugin.launchActivity=as[0].name;
			}

			for(ActivityInfo ai:as)
			{
				plugin.aisMap.put(ai.name,ai);
				//as[0].
			}
		}

		String dexOutputPath=getDexFile(packageName,context).getAbsolutePath();
		ClassLoader parent=ClassLoader.getSystemClassLoader();
		DexClassLoader loader=new DexClassLoader(plugin.apkPath,dexOutputPath,null,parent);
		plugin.loader=loader;

		try
		{
			Class<?> pClass=plugin.loader.loadClass(plugin.launchActivity);
			plugin.pClass=pClass;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		put(plugin.packageName,plugin);

		return plugin;
	}

	/**
	 * 启动插件
	 * 
	 * @param plugin
	 * @param context
	 */
	public void launch(Context context,Plugin plugin)
	{
		if(TextUtils.isEmpty(plugin.ai.className))
		{
			plugin.ai.className="android.app.Application";
		}
		Instrumentation instrumentation=new Instrumentation();
		try
		{
			ActivityThread mActivityThread=ActivityThread.currentActivityThread();
			Class<?> clazz=Class.forName("android.app.ContextImpl");
			Method createAppContext=clazz.getDeclaredMethod("createAppContext",ActivityThread.class,LoadedApk.class);
			createAppContext.setAccessible(true);
			Object appContext=createAppContext.invoke(null,mActivityThread,plugin.loadedApk);

			// plugin.app=loadedApk.makeApplication(false,instrumentation);
			// ContextImpl
			// appContext=ContextImpl.createAppContext(mActivityThread,this);
			// plugin.app=mActivityThread.mInstrumentation.newApplication(cl,appClass,appContext);

			plugin.app=instrumentation.newApplication(plugin.loader,plugin.ai.className,(Context)appContext);
			Method setOuterContext=clazz.getDeclaredMethod("setOuterContext",Context.class);
			setOuterContext.setAccessible(true);
			setOuterContext.invoke(appContext,plugin.app);
			// appContext.setOuterContext(app);
			instrumentation.callApplicationOnCreate(plugin.app);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		Intent intent=new Intent(context,plugin.pClass);
		intent.putExtra("plugin",plugin.packageName);
		if(context instanceof Activity)
		{
			((Activity)context).startActivity(intent);
		}
		else
		{
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	// private AssetManager createAssetManager(Context context,String apkPath)
	// {
	// try
	// {
	// Class<AssetManager> clazz=AssetManager.class;
	// AssetManager assetManager=clazz.newInstance();
	//
	// Method method=clazz.getDeclaredMethod("addAssetPath",String.class);
	// // int SDK=Build.VERSION.SDK_INT;
	// // if(SDK<=15)
	// // {
	// // String
	// // dexPath=context.getApplicationContext().getPackageCodePath();
	// // method.invoke(assetManager,dexPath);
	// // }
	// method.invoke(assetManager,apkPath);
	//
	// return assetManager;
	// }
	// catch(Exception e)
	// {
	// e.printStackTrace();
	// }
	// return null;
	// }

	private File getPluginDataDirFile(String packageName,Context context)
	{
		File root=context.getFilesDir().getParentFile();
		File pluginRoot=new File(root,"PluginData");
		if(!pluginRoot.exists())
		{
			pluginRoot.mkdirs();
		}
		File pf=new File(pluginRoot,packageName);
		if(!pf.exists())
		{
			pf.mkdirs();
		}
		return pf;
	}

	private File getApkFile(String packageName,Context context)
	{
		File dataDirFile=getPluginDataDirFile(packageName,context);
		File apkRoot=new File(dataDirFile,"apk");
		if(!apkRoot.exists())
		{
			apkRoot.mkdirs();
		}
		File apk=new File(apkRoot,packageName+".apk");
		return apk;
	}

	private File getDexFile(String packageName,Context context)
	{
		File dataDirFile=getPluginDataDirFile(packageName,context);
		File dexRoot=new File(dataDirFile,"app_dex");
		if(!dexRoot.exists())
		{
			dexRoot.mkdirs();
		}
		return dexRoot;
	}
}