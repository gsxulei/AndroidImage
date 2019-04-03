package xplugin.hook;

import java.lang.reflect.Field;

public class ClassUtils
{
	public static Object newInstance(Object o,ClassLoader loader)
	{
		Class<?> clazz=o.getClass();
		Field[] fields=clazz.getDeclaredFields();
		Object obj=null;
		try
		{
			Class<?> pClass=loader.loadClass(clazz.getName());
			obj=pClass.newInstance();

			for(Field field : fields)
			{
				field.setAccessible(true);
				String fieldName=field.getName();

				Field f=pClass.getDeclaredField(fieldName);
				f.setAccessible(true);
				f.set(obj,field.get(o));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return obj;
	}
}