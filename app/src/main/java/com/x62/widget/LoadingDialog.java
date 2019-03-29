package com.x62.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;

import commons.utils.ResUtils;
import com.x62.image.R;

public class LoadingDialog
{
	private AlertDialog dialog;

	private String text;

	public boolean interceptHome=true;

	/**
	 * 是否拦截返回键
	 */
	public boolean interceptBack=true;

	public LoadingDialog(Context context)
	{
		dialog=new AlertDialog.Builder(context).create();
		dialog.setCancelable(false);
	}

	public void show()
	{
		dialog.show();
		Window window=dialog.getWindow();
		window.setDimAmount(0f);
		window.setContentView(R.layout.widget_dialog_loading);
		window.setBackgroundDrawableResource(android.R.color.transparent);
		TextView tvLoading=(TextView)dialog.findViewById(R.id.tvLoading);
		if(!TextUtils.isEmpty(text))
		{
			tvLoading.setText(text);
		}

		dialog.setOnKeyListener(new DialogInterface.OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog,int keyCode,KeyEvent event)
			{
				if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
				{
					if(interceptBack)
					{
						return true;
					}
					dismiss();
				}
				if(keyCode==KeyEvent.KEYCODE_HOME)
				{
					if(interceptHome)
					{
						return true;
					}
				}
				return false;
			}
		});
	}

	public void setLoadingText(String text)
	{
		this.text=text;
	}

	public void setLoadingText(int resId)
	{
		this.text=ResUtils.getString(resId);
	}

	public void dismiss()
	{
		if(dialog!=null)
		{
			dialog.dismiss();
		}
	}
}