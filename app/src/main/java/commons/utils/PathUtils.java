package commons.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import commons.app.base.AppBase;

/**
 * 路径工具<br/>
 * Created by GSXL on 2017-10-24.
 */
public class PathUtils
{
	//	private static String appRootName="ZhiBen";
	//	private static String crashPathName="crash";
	//	private static String sdRoot=Environment.getExternalStorageDirectory().getAbsolutePath();
	//
	//	/**
	//	 * 获取APP在SD卡上根目录
	//	 *
	//	 * @return
	//	 */
	//	public static String getAppRoot()
	//	{
	//		File appRoot=new File(sdRoot,appRootName);
	//		if(!appRoot.exists())
	//		{
	//			appRoot.mkdirs();
	//		}
	//		return appRoot.getAbsolutePath();
	//	}
	//
	//	public static String getCrashPath()
	//	{
	//		File crashPath=new File(getAppRoot(),crashPathName);
	//		if(!crashPath.exists())
	//		{
	//			crashPath.mkdirs();
	//		}
	//		return crashPath.getAbsolutePath();
	//	}
	//
	//	public static String getShareImagePath()
	//	{
	//		File imagePath=new File(getAppRoot(),"share");
	//		if(!imagePath.exists())
	//		{
	//			imagePath.mkdirs();
	//		}
	//		return imagePath.getAbsolutePath();
	//	}
	//
	//	public static String getDBPath()
	//	{
	//		File dbPath=new File(getAppRoot(),"db");
	//		if(!dbPath.exists())
	//		{
	//			dbPath.mkdirs();
	//		}
	//		return dbPath.getAbsolutePath();
	//	}
	private static String sdRoot=Environment.getExternalStorageDirectory().getAbsolutePath();
	private static final String APP_ROOT_NAME="ZhiBen";
	private static final String APP_CRASH_NAME="crash";
	private static final String APP_TEMP_NAME="temp";
	private static final String APP_CACHE_NAME="cache";
	private static final String APP_CAMERA_NAME="camera";

	private static File appDataFile;
	private static File appCacheFile;

	static
	{
		Context context=AppBase.getInstance().getApplication();
		appDataFile=context.getFilesDir();
		appCacheFile=context.getCacheDir();
	}

	/**
	 * 获取APP在SD卡上根目录
	 *
	 * @return APP在SD卡上根目录
	 */
	private static String getAppRoot()
	{
		return getPath(sdRoot,APP_ROOT_NAME);
	}

	public static String getCrashPath()
	{
		return getPath(getAppRoot(),APP_CRASH_NAME);
	}

	public static String getTempPath()
	{
		return getPath(getAppRoot(),APP_TEMP_NAME);
	}

	public static String getCachePath()
	{
		return getPath(getAppRoot(),APP_CACHE_NAME);
	}

	public static String getCameraPath()
	{
		return getPath(getAppRoot(),APP_CAMERA_NAME);
	}

	private static String getPath(String dirPath,String name)
	{
		File file=new File(dirPath,name);
		if(!file.exists())
		{
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}

	/**
	 * 获取APP内部存储根路径，一般为data/data/包名/files
	 *
	 * @return APP内部存储根路径
	 */
	public static File getAppDataFile()
	{
		return appDataFile;
	}

	public static File getAppCacheFile()
	{
		return appCacheFile;
	}
}