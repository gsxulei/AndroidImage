package commons.base;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by GSXL on 2017-10-22.
 */

public abstract class BaseDialog
{
	protected AlertDialog mDialog;
	protected Context mContext;

	/**
	 * 显示弹框
	 */
	public abstract void show();

	/**
	 * 弹框消失
	 */
	public void dismiss()
	{
		if(mDialog!=null)
		{
			mDialog.dismiss();
		}
	}
}