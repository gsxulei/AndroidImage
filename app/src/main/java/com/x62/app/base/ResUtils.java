package com.x62.app.base;

import android.content.res.Resources;

public class ResUtils
{
	private static Resources res=AppBase.getInstance().getContext().getResources();

	public static String getString(int id)
	{
		return res.getString(id);
	}

	public static int getWidth()
	{
		return res.getDisplayMetrics().widthPixels;
	}
}