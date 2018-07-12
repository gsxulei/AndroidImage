package com.x62.utils;

import android.content.res.Resources;

import com.x62.app.base.AppBase;

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

	/**
	 * 获取屏幕宽度
	 *
	 * @return 屏幕宽度
	 */
	public static int getWidth()
	{
		return res.getDisplayMetrics().widthPixels;
	}

	/**
	 * 根据ID获取颜色
	 *
	 * @param id 颜色ID
	 * @return 颜色值
	 */
	public static int getColor(int id)
	{
		return res.getColor(id);
	}
}