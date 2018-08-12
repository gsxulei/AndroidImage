package com.x62.commons.base;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFragmentStatePagerAdapter<T> extends FragmentStatePagerAdapter
{
	protected ArrayList<T> data=new ArrayList<>();

	public BaseFragmentStatePagerAdapter(FragmentManager fm)
	{
		super(fm);
	}

	@Override
	public abstract Fragment getItem(int position);

	@Override
	public int getCount()
	{
		return data.size();
	}

	@Override
	public int getItemPosition(Object object)
	{
		return POSITION_NONE;
	}

	public void clear()
	{
		data.clear();
	}

	public List<T> getData()
	{
		return data;
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
}