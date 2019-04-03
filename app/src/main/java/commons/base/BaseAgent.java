package commons.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import commons.annotations.LayoutBind;

public class BaseAgent implements View.OnClickListener, Cloneable
{
	public Context mContext;

	private static Map<Class<? extends BaseAgent>,BaseAgent> pool=new HashMap<>();

	public void setContext(Context context)
	{
		mContext=context;
	}

	/**
	 * 打开页面
	 *
	 * @param clazz 页面Activity
	 */
	public void open(Class<?> clazz)
	{
		if(mContext instanceof BaseActivity)
		{
			((BaseActivity)mContext).open(clazz);
		}
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

	protected int getLayoutId()
	{
		int layoutId=0;
		LayoutBind layoutBind=getClass().getAnnotation(LayoutBind.class);
		if(layoutBind!=null)
		{
			layoutId=layoutBind.value();
		}
		return layoutId;
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

	public static void putAgent(Class<? extends BaseAgent> clazz,BaseAgent agent)
	{
		pool.put(clazz,agent);
	}

	public static BaseAgent getAgent(Class<? extends BaseAgent> clazz)
	{
		BaseAgent agent=null;
		BaseAgent b=pool.get(clazz);
		try
		{
			agent=(BaseAgent)b.clone();
		}
		catch(CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return agent;
	}

	public void onDestroy()
	{
	}

	protected void finish()
	{
		if(mContext instanceof Activity)
		{
			((Activity)mContext).finish();
		}
	}
}