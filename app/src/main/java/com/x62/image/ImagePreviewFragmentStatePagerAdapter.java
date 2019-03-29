package com.x62.image;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import commons.base.BaseFragmentStatePagerAdapter;

public class ImagePreviewFragmentStatePagerAdapter extends BaseFragmentStatePagerAdapter<String>
{
	public ImagePreviewFragmentStatePagerAdapter(FragmentManager fm)
	{
		super(fm);
	}

	@Override
	public Fragment getItem(int position)
	{
		ImagePreviewFragment fragment=new ImagePreviewFragment();
		Bundle bundle=new Bundle();
		bundle.putString("path",data.get(position));
		bundle.putInt("position",position);
		fragment.setArguments(bundle);
		return fragment;
	}
}