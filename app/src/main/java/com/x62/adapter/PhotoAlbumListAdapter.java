package com.x62.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.x62.bean.PhotoAlbumBean;
import com.x62.commons.base.BaseListAdapter;
import com.x62.commons.base.ImageLoaderWrapper;
import com.x62.image.R;

import java.io.File;

/**
 * 相册列表
 */
public class PhotoAlbumListAdapter extends BaseListAdapter<PhotoAlbumBean>
{
	private Activity activity;
	private int currAlbumPosition=0;

	public PhotoAlbumListAdapter(Activity context)
	{
		super(context);
		activity=context;
	}

	public void setCurrAlbumPosition(int currAlbumPosition)
	{
		this.currAlbumPosition=currAlbumPosition;
		notifyDataSetChanged();
	}

	@Override
	protected int getLayout()
	{
		return R.layout.item_photo_album;
	}

	@Override
	protected void onBindView(View root,int position)
	{
		ImageView ivAlbum=(ImageView)root.findViewById(R.id.ivAlbum);
		TextView tvAlbumName=(TextView)root.findViewById(R.id.tvAlbumName);
		TextView tvAlbumSum=(TextView)root.findViewById(R.id.tvAlbumSum);
		RadioButton rbAlbumSelected=(RadioButton)root.findViewById(R.id.rbAlbumSelected);

		PhotoAlbumBean bean=data.get(position);

		ImageLoaderWrapper.Options<Activity> options=new ImageLoaderWrapper.Options<>();
		options.obj=activity;
		options.file=new File(bean.photos.get(0));
		options.iv=ivAlbum;
		options.isCenterCrop=false;
		options.isCenterCrop=true;
		ImageLoaderWrapper.load(options);

		tvAlbumName.setText(bean.name);
		tvAlbumSum.setText(bean.photos.size()+"项");

		if(position==currAlbumPosition)
		{
			rbAlbumSelected.setChecked(true);
			rbAlbumSelected.setVisibility(View.VISIBLE);
		}
		else
		{
			rbAlbumSelected.setChecked(false);
			rbAlbumSelected.setVisibility(View.GONE);
		}
	}
}