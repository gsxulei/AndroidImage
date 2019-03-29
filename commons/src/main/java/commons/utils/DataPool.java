package commons.utils;

import java.util.HashMap;
import java.util.Map;

public class DataPool
{
	private Map<String,Object> map=new HashMap<>();

	private static class Loader
	{
		private static final DataPool INSTANCE=new DataPool();
	}

	private DataPool()
	{
	}

	public static DataPool getInstance()
	{
		return Loader.INSTANCE;
	}

	public void put(String key,Object Value)
	{
		map.put(key,map);
	}

	public Object remove(String key)
	{
		return map.remove(key);
	}
}