package commons.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import commons.annotations.LayoutBind;
import commons.msgbus.MsgBus;
import commons.utils.ResUtils;
import commons.utils.ViewBind;

public class BaseFragment extends Fragment implements View.OnClickListener
{
	protected View mRootView;
	//protected boolean isVisible=false;// 当前Fragment是否可见
	protected boolean isInitView=false;// 是否与View建立起映射关系
	protected boolean isFirstLoad=true;// 是否是第一次加载数据

	public Activity mContext;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext=getActivity();
		MsgBus.register(this);
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

		ViewGroup parent=(ViewGroup)mRootView.getParent();
		if(parent!=null)
		{
			parent.removeView(mRootView);
		}
		isInitView=true;

		FrameLayout layout=new FrameLayout(mContext);
		ViewGroup.LayoutParams params=mRootView.getLayoutParams();
		if(params!=null)
		{
			layout.setLayoutParams(params);
		}
		layout.addView(mRootView);
		for(View view : getCoverView(inflater,layout))
		{
			layout.addView(view);
		}

		lazyLoadData();
		return layout;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		BaseActivity.last=getClass();
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mContext=activity;
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		if(context instanceof Activity)
		{
			mContext=(Activity)context;
		}
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

	public List<View> getCoverView(LayoutInflater inflater,ViewGroup layout)
	{
		return new ArrayList<>();
	}

	public FrameLayout.LayoutParams getLayoutParams(View view,int topMarginId)
	{
		int WRAP_CONTENT=FrameLayout.LayoutParams.WRAP_CONTENT;
		FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
		params.width=view.getLayoutParams().width;
		params.height=view.getLayoutParams().height;
		if(topMarginId>0)
		{
			params.topMargin=ResUtils.getDimension(topMarginId);
		}

		return params;
	}

	public FrameLayout.LayoutParams getLayoutParams(View view)
	{
		return getLayoutParams(view,0);
	}

	public FrameLayout.LayoutParams getCenterLayoutParams(View view)
	{
		FrameLayout.LayoutParams params=getLayoutParams(view);
		params.gravity=Gravity.CENTER;

		return params;
	}
}