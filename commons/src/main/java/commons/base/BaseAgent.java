package commons.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class BaseAgent implements View.OnClickListener
{
	public Context mContext;

	@Override
	public void onClick(View v)
	{
	}

	public void initView()
	{
	}

	public void initData()
	{
	}

	/**
	 * 打开页面
	 *
	 * @param clazz  页面Activity
	 * @param bundle 参数
	 */
	public void open(Class<?> clazz,Bundle bundle)
	{
		if(mContext instanceof BaseActivity)
		{
			((BaseActivity)mContext).open(clazz,bundle);
		}
	}

	public Bundle getParamBundle()
	{
		return ((BaseActivity)mContext).getParamBundle();
	}

	/**
	 * 显示Toast
	 *
	 * @param text 显示内容
	 */
	public void toast(String text)
	{
		if(mContext instanceof BaseActivity)
		{
			((BaseActivity)mContext).toast(text);
		}
	}

	public void toast(int resId)
	{
		if(mContext instanceof BaseActivity)
		{
			((BaseActivity)mContext).toast(resId);
		}
	}
}