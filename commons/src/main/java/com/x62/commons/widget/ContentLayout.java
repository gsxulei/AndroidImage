package com.x62.commons.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.x62.commons.utils.ViewBind;

/**
 * 可添加头和尾的RecyclerView
 */
public class ContentLayout extends RecyclerView
{
	private ContentAdapter contentAdapter;

	public ContentLayout(Context context)
	{
		super(context);
		init();
	}

	public ContentLayout(Context context,@Nullable AttributeSet attrs)
	{
		super(context,attrs);
		init();
	}

	public ContentLayout(Context context,@Nullable AttributeSet attrs,int defStyle)
	{
		super(context,attrs,defStyle);
		init();
	}

	private void init()
	{
		contentAdapter=new ContentAdapter();
		//		setOverScrollMode(OVER_SCROLL_NEVER);
		//		setVerticalScrollBarEnabled(false);
	}

	@Override
	public void setAdapter(Adapter adapter)
	{
		contentAdapter.setAdapter(adapter);
		super.setAdapter(contentAdapter);
	}

	public void setAdapter()
	{
		setAdapter(null);
	}

	public void addHeader(int layoutId)
	{
		LayoutInflater mLayoutInflater=LayoutInflater.from(getContext());
		View view=mLayoutInflater.inflate(layoutId,this,false);
		contentAdapter.addHeader(view);
	}

	public void addHeader(int layoutId,Object target)
	{
		LayoutInflater mLayoutInflater=LayoutInflater.from(getContext());
		View view=mLayoutInflater.inflate(layoutId,this,false);
		contentAdapter.addHeader(view);
		ViewBind.bind(target,view);
	}
}