package commons.base;

import android.util.Base64;

import java.io.Serializable;

import commons.utils.JsonUtils;

public class BaseBean implements Serializable
{
	@Override
	public String toString()
	{
		return JsonUtils.o2s(this);
	}

	public String base64()
	{
		return Base64.encodeToString(toString().getBytes(),Base64.DEFAULT);
	}

	public static <T> T base64ToObject(String base64,Class<T> t)
	{
		String json=new String(Base64.decode(base64,Base64.DEFAULT));
		return JsonUtils.s2o(json,t);
	}
}