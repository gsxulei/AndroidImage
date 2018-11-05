package com.x62.commons.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.x62.commons.annotations.LayoutBind;
import com.x62.commons.msgbus.MsgBus;
import com.x62.commons.utils.ViewBind;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener
{
	private Toast mToast;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//注册消息
		MsgBus.register(this);

		int layoutId=getLayoutId();
		if(layoutId<=0)
		{
			return;
		}

		setContentView(layoutId);
		ViewBind.bind(this);
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

	/**
	 * 显示Toast
	 *
	 * @param text 显示内容
	 */
	public void toast(String text)
	{
		if(mToast==null)
		{
			mToast=Toast.makeText(this,"",Toast.LENGTH_SHORT);
		}
		mToast.setText(text);
		mToast.show();
	}

	public void toast(int resId)
	{
		if(mToast==null)
		{
			mToast=Toast.makeText(this,"",Toast.LENGTH_SHORT);
		}
		mToast.setText(resId);
		mToast.show();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		//解绑消息
		MsgBus.unregister(this);
	}
}