package com.x62.utils;

import android.os.Environment;

import java.io.File;

/**
 * 路径工具<br/>
 * Created by GSXL on 2017-10-24.
 */
public class PathUtils
{
	private static String appRootName="ZhiBen";
	private static String crashPathName="crash";
	private static String sdRoot=Environment.getExternalStorageDirectory().getAbsolutePath();

	/**
	 * 获取APP在SD卡上根目录
	 *
	 * @return
	 */
	public static String getAppRoot()
	{
		File appRoot=new File(sdRoot,appRootName);
		if(!appRoot.exists())
		{
			appRoot.mkdirs();
		}
		return appRoot.getAbsolutePath();
	}

	public static String getCrashPath()
	{
		File crashPath=new File(getAppRoot(),crashPathName);
		if(!crashPath.exists())
		{
			crashPath.mkdirs();
		}
		return crashPath.getAbsolutePath();
	}

	public static String getShareImagePath()
	{
		File imagePath=new File(getAppRoot(),"share");
		if(!imagePath.exists())
		{
			imagePath.mkdirs();
		}
		return imagePath.getAbsolutePath();
	}

	public static String getDBPath()
	{
		File dbPath=new File(getAppRoot(),"db");
		if(!dbPath.exists())
		{
			dbPath.mkdirs();
		}
		return dbPath.getAbsolutePath();
	}
}