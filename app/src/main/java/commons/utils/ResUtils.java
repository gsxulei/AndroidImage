package commons.utils;

import android.content.res.Resources;

import commons.app.base.AppBase;

/**
 * 资源工具类
 */
public class ResUtils
{
	private static final Resources res=AppBase.getInstance().getContext().getResources();

	//	/**
	//	 * 根据ID获取字符串
	//	 *
	//	 * @param id 字符串ID
	//	 * @return 与资源ID对应的字符串
	//	 */
	//	public static String getString(int id)
	//	{
	//		return res.getString(id);
	//	}
	//
	//	/**
	//	 * 根据ID获取带有占位符的字符串
	//	 *
	//	 * @param id   字符串ID
	//	 * @param args 占位符参数
	//	 * @return 与资源ID对应的字符串
	//	 */
	//	public static String getStringFormat(int id,Object... args)
	//	{
	//		return String.format(res.getString(id),args);
	//	}

	/**
	 * 根据ID获取带有占位符的字符串
	 *
	 * @param id   字符串ID
	 * @param args 占位符参数
	 * @return 与资源ID对应的字符串
	 */
	public static String getString(int id,Object... args)
	{
		//Resources res=AppBase.getInstance().getContext().getResources();
		if(args==null||args.length==0)
		{
			return res.getString(id);
		}
		return String.format(res.getString(id),args);
	}

	public static int getDimension(int id)
	{
		return res.getDimensionPixelSize(id);
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

	public static int getStatusBarHeight()
	{
		int result=0;
		//获取状态栏高度的资源id
		int resourceId=res.getIdentifier("status_bar_height","dimen","android");
		if(resourceId>0)
		{
			result=res.getDimensionPixelSize(resourceId);
		}
		return result;
	}
}