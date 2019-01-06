package com.x62.image;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.x62.commons.annotations.LayoutBind;
import com.x62.commons.base.BaseViewPagerFragment;
import com.x62.commons.base.ImageLoaderWrapper;
import com.x62.commons.utils.ViewBind;

import java.io.File;

@LayoutBind(id=R.layout.fragment_image_preview)
public class ImagePreviewFragment extends BaseViewPagerFragment
{
	private String path;

	@ViewBind.Bind(id=R.id.iv)
	private ImageView iv;

	@ViewBind.Bind(id=R.id.ssiv)
	private SubsamplingScaleImageView ssiv;

	private int position;

	@Override
	protected void initView(View rootView)
	{
	}

	@Override
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
	protected void initData()
	{
		Bundle bundle=getArguments();
		path=bundle.getString("path");
		position=bundle.getInt("position");

		show();
	}

	private void show()
	{
		if(TextUtils.isEmpty(path)||ssiv==null)
		{
			return;
		}

		ImageLoaderWrapper.Options<Fragment> options=new ImageLoaderWrapper.Options<>();
		options.obj=this;
		options.path=path;
		options.iv=iv;
		options.isCenterCrop=false;
		options.placeholder=0;
		ImageLoaderWrapper.load(options);

		ssiv.setImage(ImageSource.uri(Uri.fromFile(new File(path))));
		ssiv.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener()
		{
			@Override
			public void onReady()
			{
			}

			@Override
			public void onImageLoaded()
			{
				iv.setVisibility(View.GONE);
			}

			@Override
			public void onPreviewLoadError(Exception e)
			{
			}

			@Override
			public void onImageLoadError(Exception e)
			{
			}

			@Override
			public void onTileLoadError(Exception e)
			{
			}

			@Override
			public void onPreviewReleased()
			{
			}
		});
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(ssiv!=null)
		{
			ssiv.recycle();
		}
		Log.e("xulei","onDestroy->"+position);
	}
}