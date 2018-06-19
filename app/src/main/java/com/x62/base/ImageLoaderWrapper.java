package com.x62.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.x62.image.R;

import java.io.File;

/**
 * 图片加载器封装
 */
public class ImageLoaderWrapper
{
	/**
	 * 参数选项
	 */
	public static class Options
	{
		public Activity activity;
		public Fragment fragment;
		public android.support.v4.app.Fragment v4Fragment;
		public Context context;
		public File file;
		public ImageView iv;
		public int placeholder=R.mipmap.ic_launcher;
		public int error=R.mipmap.ic_launcher;
		public boolean isCenterCrop=true;
	}

	public static void load(Options options)
	{
		glideLoad(options);
	}

	/**
	 * 使用Glide加载图片
	 *
	 * @param options
	 */
	private static void glideLoad(Options options)
	{
		RequestManager manager=null;
		if(options.activity!=null)
		{
			manager=Glide.with(options.activity);
		}
		else if(options.fragment!=null)
		{
			manager=Glide.with(options.fragment);
		}
		else if(options.v4Fragment!=null)
		{
			manager=Glide.with(options.v4Fragment);
		}
		else if(options.context!=null)
		{
			manager=Glide.with(options.context);
		}

		if(manager==null)
		{
			return;
		}

		RequestOptions reqOptions=new RequestOptions();
		if(options.placeholder>0)
		{
			reqOptions.placeholder(options.placeholder);
		}
		if(options.isCenterCrop)
		{
			reqOptions.centerCrop();
		}
		reqOptions.error(options.error);

		RequestBuilder builder=manager.load(options.file);
		builder.apply(reqOptions);
		builder.into(options.iv);

		//manager.load(options.file).placeholder(options.placeholder).centerCrop().error(options.error).into(options
		// .iv);
	}
}