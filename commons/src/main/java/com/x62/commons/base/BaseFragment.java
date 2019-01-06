package com.x62.commons.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.x62.commons.annotations.LayoutBind;
import com.x62.commons.msgbus.MsgBus;
import com.x62.commons.utils.ViewBind;

public abstract class BaseFragment extends Fragment implements View.OnClickListener
{
	private View rootView;
	//protected boolean isVisible=false;// 当前Fragment是否可见
	protected boolean isInitView=false;// 是否与View建立起映射关系
	protected boolean isFirstLoad=true;// 是否是第一次加载数据

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		int layoutId=getLayoutId();
		if(layoutId<=0)
		{
			return null;
		}
		if(rootView==null)
		{
			rootView=inflater.inflate(layoutId,container,false);
			//			ViewBind.bind(this,rootView);
			//			initView(rootView);
		}
		ViewBind.bind(this,rootView);
		initView(rootView);
		MsgBus.register(this);

		ViewGroup parent=(ViewGroup)rootView.getParent();
		if(parent!=null)
		{
			parent.removeView(rootView);
		}
		// onHiddenChanged(false);
		isInitView=true;
		lazyLoadData();
		return rootView;
	}

	//	@Override
	//	public void setUserVisibleHint(boolean isVisibleToUser)
	//	{
	//		super.setUserVisibleHint(isVisibleToUser);
	//		isVisible=isVisibleToUser;
	//		if(isVisibleToUser)
	//		{
	//			lazyLoadData();
	//		}
	//	}

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

	private int getLayoutId()
	{
		int layoutId=0;
		LayoutBind layoutBind=getClass().getAnnotation(LayoutBind.class);
		if(layoutBind!=null)
		{
			layoutId=layoutBind.id();
		}
		return layoutId;
	}

	@Override
	public void onClick(View v)
	{
	}

	protected abstract void initView(View rootView);

	/**
	 * 初始化数据
	 */
	protected abstract void initData();
}