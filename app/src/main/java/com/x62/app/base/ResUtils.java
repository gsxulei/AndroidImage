package com.x62.app.base;

import android.content.res.Resources;

/**
 * 资源工具类
 */
public class ResUtils
{
	private static Resources res=AppBase.getInstance().getContext().getResources();

	/**
	 * 根据ID获取字符串
	 *
	 * @param id 字符串ID
	 * @return 与资源ID对应的字符串
	 */
	public static String getString(int id)
	{
		return res.getString(id);
	}

	public static int getWidth()
	{
		return res.getDisplayMetrics().widthPixels;
	}

	public static int getColor(int id)
	{
		return res.getColor(id);
	}
}