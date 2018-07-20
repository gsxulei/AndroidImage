package com.x62.commons.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.x62.commons.app.base.AppBase;

/**
 * 获取屏幕信息工具类
 */
public class ScreenUtils
{
	private WindowManager windowManager;
	private DisplayMetrics metrics;

	/**
	 * 屏幕宽度,单位为px
	 */
	private int width=0;

	/**
	 * 屏幕高度,单位为px
	 */
	private int height=0;

	/**
	 * 屏幕密度,单位为dpi
	 */
	private int densityDpi;

	/**
	 * 缩放系数,值为 densityDpi/160
	 */
	private float scale=1;

	/**
	 * 文字缩放系数,同scale
	 */
	private float fontScale=1.0F;

	private AppBase app=AppBase.getInstance();

	private static class Loader
	{
		private static final ScreenUtils INSTANCE=new ScreenUtils();
	}

	private ScreenUtils()
	{
		init();
	}

	public static ScreenUtils getInstance()
	{
		return Loader.INSTANCE;
	}

	private void init()
	{
		try
		{
			windowManager=(WindowManager)app.getContext().getSystemService(Context.WINDOW_SERVICE);
			//windowManager=ManagerUtil.getInstance().getWindowManager();
			metrics=new DisplayMetrics();
			windowManager.getDefaultDisplay().getMetrics(metrics);
			width=metrics.widthPixels;
			height=metrics.heightPixels;
			densityDpi=metrics.densityDpi;
			scale=metrics.density;
			fontScale=metrics.scaledDensity;
			int SDK=Build.VERSION.SDK_INT;
			Display display=windowManager.getDefaultDisplay();
			if(SDK >= 14&&SDK<17)
			{
				width=(Integer)Display.class.getMethod("getRawWidth").invoke(display);
				height=(Integer)Display.class.getMethod("getRawHeight").invoke(display);
			}
			else if(SDK >= 17)
			{
				Point realSize=new Point();
				//Display.class.getMethod("getRealSize",Point.class).invoke(display,realSize);
				display.getRealSize(realSize);
				width=realSize.x;
				height=realSize.y;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取屏幕宽度
	 *
	 * @return
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * 获取屏幕高度
	 *
	 * @return
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * 获取屏幕密度,单位dpi
	 *
	 * @return
	 */
	public int getDensityDpi()
	{
		return densityDpi;
	}

	/**
	 * 获取缩放系数,值为 densityDpi/160
	 *
	 * @return
	 */
	public float getScale()
	{
		return scale;
	}


	/**
	 * 获取文字缩放系数,同scale
	 *
	 * @return
	 */
	public float getFontScale()
	{
		return fontScale;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public int dip2px(float dpValue)
	{
		//final float scale=metrics.density;
		//final float scale=context.getResources().getDisplayMetrics().density;
		return (int)(dpValue*scale+0.5F);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public int px2dip(float pxValue)
	{
		//final float scale=context.getResources().getDisplayMetrics().density;
		//final float scale=metrics.density;
		return (int)(pxValue/scale+0.5F);
	}

	/**
	 * sp转成px
	 *
	 * @param spValue
	 * @param type
	 * @return
	 */
	public float sp2px(float spValue,int type)
	{
		//final float scaledDensity=metrics.scaledDensity;
		switch(type)
		{
			case 0:
				return spValue*fontScale;
			case 1:
				return spValue*fontScale*10.0F/18.0F;
			default:
				return spValue*fontScale;
		}
	}

	public void setDpi()
	{
		setDpi(360);
	}

	public void setDpi(float wDpi)
	{
		//float wDpi=360;
		//Resources res=context.getResources();
		//Resources res=Resources.getSystem();
		Resources res=app.getContext().getResources();
		DisplayMetrics dm=res.getDisplayMetrics();
		Configuration conf=res.getConfiguration();
		Log.e("xulei","初始densityDpi->"+conf.densityDpi);
		Log.e("xulei","初始density->"+dm.density);
		Log.e("xulei","初始widthPixels->"+dm.widthPixels);
		Log.e("xulei","初始heightPixels->"+dm.heightPixels);
		Log.e("xulei","初始screenWidthDp->"+conf.screenWidthDp);
		Log.e("xulei","初始screenHeightDp->"+conf.screenHeightDp);
		Log.e("xulei","-------------------------------");
		float density=dm.widthPixels/wDpi;
		int densityDpi=(int)(density*DisplayMetrics.DENSITY_DEFAULT);
		if(densityDpi!=conf.densityDpi)
		{
			conf.densityDpi=densityDpi;
			res.updateConfiguration(conf,dm);
		}

		Log.e("xulei","densityDpi->"+conf.densityDpi);
		Log.e("xulei","density->"+dm.density);
	}
}