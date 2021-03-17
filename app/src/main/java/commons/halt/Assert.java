package commons.halt;

import android.text.TextUtils;

/**
 * 断言类
 */
public class Assert
{
	public static void orHalt(Object... obj)
	{
		boolean value=false;
		for(Object o : obj)
		{
			value=o==null;
			if(value)
			{
				break;
			}
		}
		halt(value);
	}

	public static void halt(Object obj)
	{
		halt(obj==null);
	}

	public static void halt(Object obj,String text)
	{
		halt(obj==null,text);
	}

	public static void halt(Object obj,int id)
	{
		halt(obj==null,id);
	}

	/**
	 * 当字符串为空时停止往下执行,并弹出Toast
	 *
	 * @param text 待判断字符串
	 * @param msg  Toast提示文字
	 */
	public static void halt(String text,String msg)
	{
		halt(TextUtils.isEmpty(text),msg);
	}

	/**
	 * 当字符串为空时停止往下执行
	 *
	 * @param text 待判断字符串
	 */
	public static void halt(String text)
	{
		halt(TextUtils.isEmpty(text));
	}

	public static void halt(String text,int id)
	{
		halt(TextUtils.isEmpty(text),id);
	}

	public static void halt(Boolean value,int id)
	{
		halt(value!=null&&value,null,id);
	}

	public static void halt(boolean value)
	{
		halt(value,null,0);
	}

	public static void halt(boolean value,String text)
	{
		halt(value,text,0);
	}

	public static void halt(boolean value,int id)
	{
		halt(value,null,id);
	}

	private static void halt(boolean value,String text,int id)
	{
		if(value)
		{
			throw new HaltException(text,id);
		}
	}
}