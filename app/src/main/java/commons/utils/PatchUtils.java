package commons.utils;

import android.app.Application;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

/**
 * 补丁工具
 */
public class PatchUtils
{
	private static void addDexElements(List<Object> list,Object dexElements)
	{
		int len=Array.getLength(dexElements);
		for(int i=0;i<len;i++)
		{
			Object obj=Array.get(dexElements,i);
			boolean flag=false;
			for(Object o : list)
			{
				if(o.toString().equals(obj.toString()))
				{
					flag=true;
					break;
				}
			}
			if(flag)
			{
				continue;
			}
			list.add(obj);
		}
	}

	private static Field getField(Class<?> clazz,String name) throws Exception
	{
		Field field=clazz.getDeclaredField(name);
		field.setAccessible(true);
		return field;
	}

	private static Object getDexElements(Object classLoader)
	{
		Object result=null;
		try
		{
			Field field=getField(BaseDexClassLoader.class,"pathList");
			Object pathList=field.get(classLoader);
			field=getField(pathList.getClass(),"dexElements");
			result=field.get(pathList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 加载补丁
	 *
	 * @param app Application
	 */
	public static void loadPatch(Application app)
	{
		File patch=PathUtils.getAppDataFile("patch");

		File[] files=patch.listFiles();
		if(files==null||files.length==0)
		{
			return;
		}

		File patchOpt=PathUtils.getAppDataFile("patchOpt");

		String optimizedDirectory=patchOpt.getAbsolutePath();
		ClassLoader parent=app.getClassLoader();

		Object appDexElements=getDexElements(app.getClassLoader());
		List<Object> arr=new ArrayList<>();

		for(File file : files)
		{
			String path=file.getAbsolutePath();
			if(!path.endsWith("dex"))
			{
				continue;
			}
			DexClassLoader dex=new DexClassLoader(path,optimizedDirectory,"",parent);
			Object dexElements=getDexElements(dex);
			addDexElements(arr,dexElements);
		}

		addDexElements(arr,appDexElements);

		int length=arr.size();
		Object all=Array.newInstance(appDexElements.getClass().getComponentType(),length);
		for(int i=0;i<length;i++)
		{
			Object value=arr.get(i);
			Array.set(all,i,value);
		}

		try
		{
			Field field=getField(BaseDexClassLoader.class,"pathList");
			Object pathList=field.get(app.getClassLoader());
			field=getField(pathList.getClass(),"dexElements");
			field.set(pathList,all);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void patchUp(Application app,String[] paths)
	{
		if(paths==null||paths.length==0)
		{
			return;
		}

		File patch=PathUtils.getAppDataFile("patch");
		for(String path : paths)
		{
			File from=new File(path);
			String ext=IOUtils.getFileExt(from.getName());
			String md5=Utils.md5(from);
			File to=new File(patch,md5+"."+ext);
			if(!to.exists())
			{
				IOUtils.copy(from,to);
			}
		}
		loadPatch(app);
	}
}