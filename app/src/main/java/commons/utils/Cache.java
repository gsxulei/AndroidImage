package commons.utils;

import commons.app.base.AppBase;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by GSXL on 2017-09-10. 封装SharedPreferences
 */
public class Cache
{
	private SharedPreferences sp;

	private static class Loader
	{
		private static final Cache INSTANCE=new Cache();
	}

	private Cache()
	{
		Context mCtx=AppBase.getInstance().getContext();
		sp=mCtx.getSharedPreferences("cache",Context.MODE_PRIVATE);
	}

	public static Cache getInstance()
	{
		return Loader.INSTANCE;
	}

	public <T> void save(String key,T value)
	{
		SharedPreferences.Editor editor=sp.edit();
		if(value.getClass()==String.class)
		{
			editor.putString(key,(String)value);
		}
		else if(value.getClass()==int.class||value.getClass()==Integer.class)
		{
			editor.putInt(key,(Integer)value);
		}
		else if(value.getClass()==long.class||value.getClass()==Long.class)
		{
			editor.putLong(key,(Long)value);
		}
		else if(value.getClass()==float.class||value.getClass()==Float.class)
		{
			editor.putFloat(key,(Float)value);
		}
		else if(value.getClass()==boolean.class||value.getClass()==Boolean.class)
		{
			editor.putBoolean(key,(Boolean)value);
		}
		editor.commit();
	}

	// public void save(String key,String value)
	// {
	// SharedPreferences.Editor editor=sp.edit();
	// editor.putString(key,value);
	// editor.commit();
	// }
	//
	// public void save(String key,int value)
	// {
	// SharedPreferences.Editor editor=sp.edit();
	// editor.putInt(key,value);
	// editor.commit();
	// }
	//
	// public void save(String key,long value)
	// {
	// SharedPreferences.Editor editor=sp.edit();
	// editor.putLong(key,value);
	// editor.commit();
	// }
	//
	// public void save(String key,float value)
	// {
	// SharedPreferences.Editor editor=sp.edit();
	// editor.putFloat(key,value);
	// editor.commit();
	// }
	//
	// public void save(String key,boolean value)
	// {
	// SharedPreferences.Editor editor=sp.edit();
	// editor.putBoolean(key,value);
	// editor.commit();
	// }

	public String read(String key,String defValue)
	{
		return sp.getString(key,defValue);
	}

	public String read(String key)
	{
		return read(key,"");
	}

	public int read(String key,int defValue)
	{
		return sp.getInt(key,defValue);
	}

	public int readInt(String key)
	{
		return read(key,0);
	}

	public long read(String key,long defValue)
	{
		return sp.getLong(key,defValue);
	}

	public long readLong(String key)
	{
		return read(key,0L);
	}

	public float read(String key,float defValue)
	{
		return sp.getFloat(key,defValue);
	}

	public float readFloat(String key)
	{
		return read(key,0F);
	}

	public boolean read(String key,boolean defValue)
	{
		return sp.getBoolean(key,defValue);
	}

	public boolean readBoolean(String key)
	{
		return read(key,false);
	}
}