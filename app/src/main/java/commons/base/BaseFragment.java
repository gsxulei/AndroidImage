package commons.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import commons.annotations.LayoutBind;
import commons.msgbus.MsgBus;
import commons.utils.ViewBind;

public class BaseFragment extends Fragment implements View.OnClickListener
{
	protected View mRootView;
	protected boolean isVisible=false;// 当前Fragment是否可见
	protected boolean isInitView=false;// 是否与View建立起映射关系
	protected boolean isFirstLoad=true;// 是否是第一次加载数据

	public Activity mContext;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext=getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		int layoutId=getLayoutId();
		if(layoutId<=0)
		{
			return null;
		}
		if(mRootView==null)
		{
			mRootView=inflater.inflate(layoutId,container,false);
		}
		ViewBind.bind(this,mRootView);
		initView();
		MsgBus.register(this);

		ViewGroup parent=(ViewGroup)mRootView.getParent();
		if(parent!=null)
		{
			parent.removeView(mRootView);
		}
		isInitView=true;
		lazyLoadData();
		return mRootView;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		BaseActivity.last=getClass();
	}

	/**
	 * 懒加载,子类可按需重写
	 */
	public void lazyLoadData()
	{
		if(!isFirstLoad||!isInitView)
		{
			return;
		}
		initData();
		isFirstLoad=false;
	}

	@Override
	public void onClick(View v)
	{
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		MsgBus.unregister(this);
	}

	public boolean onBackPressed()
	{
		return false;
	}

	private int getLayoutId()
	{
		int layoutId=0;
		LayoutBind layoutBind=getClass().getAnnotation(LayoutBind.class);
		if(layoutBind!=null)
		{
			layoutId=layoutBind.value();
		}
		return layoutId;
	}

	protected void initView()
	{
	}

	/**
	 * 初始化数据
	 */
	protected void initData()
	{
	}

	protected void open(Class<? extends Fragment> clazz)
	{
		((BaseActivity)mContext).open(clazz);
	}

	protected void open(Class<? extends Fragment> clazz,Bundle bundle)
	{
		((BaseActivity)mContext).open(clazz,bundle);
	}

	/**
	 * 显示Toast
	 *
	 * @param text 显示内容
	 */
	protected void toast(String text)
	{
		if(mContext instanceof BaseActivity)
		{
			((BaseActivity)mContext).toast(text);
		}
	}

	protected void toast(int resId)
	{
		if(mContext instanceof BaseActivity)
		{
			((BaseActivity)mContext).toast(resId);
		}
	}
}