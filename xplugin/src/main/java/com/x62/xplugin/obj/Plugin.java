package com.x62.xplugin.obj;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.app.LoadedApk;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import dalvik.system.DexClassLoader;

public class Plugin
{
	public Class<?> pClass;
	public String launchActivity;
	public DexClassLoader loader;
	public String packageName;
	public Resources resources;
	public AssetManager assetManager;
	public int themeResource;

	public Map<String,ActivityInfo> aisMap=new HashMap<String,ActivityInfo>();
	public ApplicationInfo ai;
	public Application app;
	public LoadedApk loadedApk;
	public File dataDirFile;
	public String apkPath;
}