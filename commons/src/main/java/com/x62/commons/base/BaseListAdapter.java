package com.x62.commons.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 对ListView的BaseAdapter进行封装
 *
 * @param <T>
 */
public abstract class BaseListAdapter<T> extends BaseAdapter
{
	protected ArrayList<T> data=new ArrayList<>();
	protected Context context;
	private LayoutInflater mLayoutInflater;

	public BaseListAdapter(Context context)
	{
		this.context=context;
		mLayoutInflater=LayoutInflater.from(context);
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		if(convertView==null)
		{
			convertView=mLayoutInflater.inflate(getLayout(),parent,false);
		}
		onBindView(convertView,position);
		return convertView;
	}

	@Override
	public int getCount()
	{
		return data.size();
	}

	@Override
	public T getItem(int position)
	{
		return data.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	public void clear()
	{
		data.clear();
	}

	public void setData(List<T> data)
	{
		clear();
		addData(data);
	}

	public void addData(List<T> data)
	{
		this.data.addAll(data);
		notifyDataSetChanged();
	}

	public List<T> getData()
	{
		return data;
	}

	protected abstract int getLayout();

	protected abstract void onBindView(View root,int position);
}