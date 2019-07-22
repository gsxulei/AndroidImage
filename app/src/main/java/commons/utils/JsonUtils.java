package commons.utils;

import com.google.gson.Gson;

import java.io.File;

/**
 * JSON工具
 *
 * @author GSXL
 */
public class JsonUtils
{
	public static Gson getGson()
	{
		Gson gson=new Gson();
		return gson;
	}

	/**
	 * 对象转字符串
	 *
	 * @param t JSON对象
	 * @return T
	 */
	public static <T> String o2s(T t)
	{
		Gson gson=getGson();
		return gson.toJson(t);
	}

	public static <T> T s2o(String json,Class<T> t)
	{
		Gson gson=new Gson();
		// gson.fromJson(json,String.class);
		return gson.fromJson(json,t);
	}

	public static <T> T f2o(String path,Class<T> t)
	{
		String json=IOUtils.readFile(path);
		return s2o(json,t);
	}

	public static <T> T f2o(File file,Class<T> t)
	{
		return f2o(file.getAbsolutePath(),t);
	}

	public static void o2f(File file,Object obj)
	{
		o2f(file.getAbsolutePath(),obj);
	}

	public static void o2f(String path,Object obj)
	{
		String content=o2s(obj);
		IOUtils.write(path,content);
	}
}