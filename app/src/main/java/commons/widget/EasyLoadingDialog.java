package commons.widget;

import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import commons.halt.Assert;
import commons.widget.utils.EasyShow;
import commons.widget.utils.TopActivity;

/**
 * Loading框工具,可在任意线程显示和关闭Loading框
 */
public class EasyLoadingDialog
{
	private static final EasyLoadingDialog sLoading=new EasyLoadingDialog();

	/**
	 * Loading框计数器,当值为1时显示,当值为0时关闭
	 */
	private static int sCount;

	private AlertDialog mDialog;

	/**
	 * 显示Loading框,仅供内部调用
	 *
	 * @param text       Loading框上显示的文字
	 * @param layoutId   布局ID
	 * @param textViewId Loading框文字控件ID
	 */
	private void showDialog(String text,int layoutId,int textViewId)
	{
		Assert.halt(TopActivity.get()==null||layoutId<=0);

		if(sCount>0)
		{
			initView(text,textViewId);
			return;
		}

		mDialog=new AlertDialog.Builder(TopActivity.get()).create();
		mDialog.setCancelable(false);
		mDialog.show();

		Window window=mDialog.getWindow();
		Assert.halt(window==null);

		window.setDimAmount(0f);
		window.setContentView(layoutId);
		window.setBackgroundDrawableResource(android.R.color.transparent);
		initView(text,textViewId);
	}

	private void initView(String text,int textViewId)
	{
		sCount++;
		Assert.halt(mDialog==null||TextUtils.isEmpty(text)||textViewId==0);

		Window window=mDialog.getWindow();
		Assert.halt(window==null);

		//这里不用做判空
		View decorView=window.getDecorView();
		TextView textView=(TextView)decorView.findViewById(textViewId);
		Assert.halt(textView);

		textView.setText(text);
	}

	private void dismissDialog()
	{
		Assert.halt(mDialog);
		sCount--;
		Assert.halt(sCount!=0);

		//Dialog内部会判断有没有显示
		mDialog.dismiss();
		mDialog=null;
	}

	/**
	 * 显示Loading框
	 *
	 * @param layoutId 布局ID
	 */
	public static void show(int layoutId)
	{
		show("",layoutId,0);
	}

	/**
	 * 显示Loading框
	 *
	 * @param text       Loading框上显示的文字
	 * @param layoutId   布局ID
	 * @param textViewId Loading框文字控件ID
	 */
	public static void show(String text,int layoutId,int textViewId)
	{
		EasyShow.post(()->sLoading.showDialog(text,layoutId,textViewId));
	}

	/**
	 * 关闭Loading框
	 */
	public static void dismiss()
	{
		EasyShow.post(sLoading::dismissDialog);
	}
}