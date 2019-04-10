package commons.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.x62.image.R;

import commons.base.BaseDialog;
import commons.utils.ResUtils;
import commons.utils.ViewBind;

/**
 * 询问对话框<br/>
 * 提供确定/取消两种操作<br/>
 */
public class AskDialog extends BaseDialog implements View.OnClickListener
{
	@ViewBind.Bind(id=R.id.commons_dialog_ask_content)
	private TextView mContent;

	private String content;

	private OnOperateListener listener;

	public AskDialog(Context context)
	{
		mContext=context;
	}

	@Override
	public void show()
	{
		mDialog=new AlertDialog.Builder(mContext).create();
		mDialog.setCancelable(false);
		Window window=mDialog.getWindow();

		if(window==null)
		{
			return;
		}

		mDialog.show();

		window.setContentView(R.layout.commons_widget_dialog_ask);
		//window.getDecorView().setPadding(0,0,0,0);
		WindowManager.LayoutParams params=window.getAttributes();
		params.width=ResUtils.getDimension(R.dimen.commons_dialog_ask_width);
		params.height=ResUtils.getDimension(R.dimen.commons_dialog_ask_height);
		window.setAttributes(params);
		window.setBackgroundDrawableResource(R.drawable.commons_bg_dialog_ask);
		//		try
		//		{
		//			//华为手机默认自带圆角白色背景
		//			window.setBackgroundDrawableResource(android.R.color.transparent);
		//		}
		//		catch(Exception e)
		//		{
		//			e.printStackTrace();
		//		}

		ViewBind.bind(this,window.getDecorView());

		if(!TextUtils.isEmpty(content))
		{
			mContent.setText(content);
		}
	}

	public void setContent(String content)
	{
		this.content=content;
	}

	public void setContent(int id)
	{
		this.content=ResUtils.getString(id);
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.commons_dialog_ask_cancel:
			{
				dismiss();
				if(listener!=null)
				{
					listener.onCancel();
				}
			}
			break;

			case R.id.commons_dialog_ask_confirm:
			{
				dismiss();
				if(listener!=null)
				{
					listener.onConfirm();
				}
			}
			break;
		}
	}

	public void setOnOperateListener(OnOperateListener listener)
	{
		this.listener=listener;
	}

	public static class DefaultOnOperateListener implements OnOperateListener
	{
		@Override
		public void onConfirm()
		{
		}

		@Override
		public void onCancel()
		{
		}
	}

	public interface OnOperateListener
	{
		void onConfirm();

		void onCancel();
	}
}