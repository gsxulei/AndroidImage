package com.x62;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by GSXL on 2018-05-05.
 */

public class AndroidApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		setDpi(this);
	}

	public void setDpi(Context context)
	{
		float wDpi=360;
		Resources res=context.getResources();
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