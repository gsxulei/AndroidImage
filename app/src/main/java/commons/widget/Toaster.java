package commons.widget;

import android.app.Activity;
import android.widget.Toast;

import commons.widget.utils.EasyShow;
import commons.widget.utils.TopActivity;

/**
 * Toast工具,可在任意线程显示Toast
 */
public class Toaster
{
	public static void show(String text)
	{
		Activity act=TopActivity.get();
		if(act==null)
		{
			return;
		}
		EasyShow.post(()->Toast.makeText(act,text,Toast.LENGTH_LONG).show());
	}

	public static void show(int resId)
	{
		Activity act=TopActivity.get();
		if(act!=null)
		{
			show(act.getString(resId));
		}
	}
}