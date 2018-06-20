package com.x62.pick;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.x62.app.base.ResUtils;
import com.x62.base.BaseListAdapter;
import com.x62.base.BaseRecyclerViewAdapter;
import com.x62.base.ImageLoaderWrapper;
import com.x62.image.R;

import java.io.File;

public class PhotoListAdapter extends BaseRecyclerViewAdapter<String,PhotoListAdapter.ViewHolder>
{
	private int width;

	public PhotoListAdapter(Context context)
	{
		super(context);
		width=ResUtils.getWidth()/4;
	}

	@Override
	protected int getLayout()
	{
		return R.layout.item_photo;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
	{
		return new ViewHolder(mLayoutInflater.inflate(getLayout(),null,false));
	}

	@Override
	public void onBindViewHolder(ViewHolder holder,int position)
	{
		super.onBindViewHolder(holder,position);

		holder.itemView.setLayoutParams(new FrameLayout.LayoutParams(width,width));
		ImageLoaderWrapper.Options options=new ImageLoaderWrapper.Options();
		options.activity=(Activity)context;
		options.file=new File(data.get(position));
		options.iv=holder.ivPhotoItem;
		ImageLoaderWrapper.load(options);
	}


	public static class ViewHolder extends RecyclerView.ViewHolder
	{
		public ImageView ivPhotoItem;

		public ViewHolder(View itemView)
		{
			super(itemView);

			ivPhotoItem=(ImageView)itemView.findViewById(R.id.ivPhotoItem);
		}
	}
	//	@Override
	//	protected int getLayout()
	//	{
	//		return R.layout.item_photo;
	//	}
	//
	//	@Override
	//	protected void onBindView(View root,int position)
	//	{
	//		View flPhotoItem=root.findViewById(R.id.flPhotoItem);
	//		flPhotoItem.setLayoutParams(new FrameLayout.LayoutParams(width,width));
	//
	//		ImageView ivPhotoItem=(ImageView)root.findViewById(R.id.ivPhotoItem);
	//		ImageLoaderWrapper.Options options=new ImageLoaderWrapper.Options();
	//		options.activity=(Activity)context;
	//		options.file=new File(data.get(position));
	//		options.iv=ivPhotoItem;
	//		ImageLoaderWrapper.load(options);
	//	}
}