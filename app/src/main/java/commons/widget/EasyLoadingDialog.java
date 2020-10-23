package commons.widget;

import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import commons.widget.utils.EasyShow;
import commons.widget.utils.TopActivity;

/**
 * Loading框工具,可在任意线程显示Loading框
 */
public class EasyLoadingDialog
{
	private static final EasyLoadingDialog sLoading=new EasyLoadingDialog();
	private static int sCount;

	private AlertDialog mDialog;

	private void showDialog(String text,int layoutId,int textViewId)
	{
		if(TopActivity.get()==null||layoutId<=0)
		{
			return;
		}

		sCount++;
		if(sCount>1)
		{
			initView(text,textViewId);
			return;
		}

		mDialog=new AlertDialog.Builder(TopActivity.get()).create();
		mDialog.setCancelable(false);
		mDialog.show();

		Window window=mDialog.getWindow();
		if(window==null)
		{
			return;
		}

		window.setDimAmount(0f);
		window.setContentView(layoutId);
		window.setBackgroundDrawableResource(android.R.color.transparent);
		initView(text,textViewId);
	}

	private void initView(String text,int textViewId)
	{
		if(mDialog==null||TextUtils.isEmpty(text))
		{
			return;
		}

		Window window=mDialog.getWindow();
		if(window==null)
		{
			return;
		}

		View decorView=window.getDecorView();
		if(decorView==null)
		{
			return;
		}

		TextView textView=(TextView)decorView.findViewById(textViewId);
		if(textView==null)
		{
			return;
		}
		textView.setText(text);
	}

	private void dismissDialog()
	{
		if(mDialog==null)
		{
			return;
		}
		sCount--;
		if(sCount<=0)
		{
			sCount=0;
			mDialog.dismiss();
		}
	}

	public static void show(String text,int layoutId)
	{
		show(text,layoutId,0);
	}

	public static void show(String text,int layoutId,int textViewId)
	{
		EasyShow.post(()->sLoading.showDialog(text,layoutId,textViewId));
	}

	public static void dismiss()
	{
		EasyShow.post(sLoading::dismissDialog);
	}
}