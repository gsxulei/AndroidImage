package com.x62.commons.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

/**
 * 图片加载器封装
 */
public class ImageLoaderWrapper
{
	public static int defaultPlaceholder;
	public static int defaultError;

	public static void setDefault(int id)
	{
		defaultPlaceholder=id;
		defaultError=id;
	}

	/**
	 * 参数选项
	 */
	public static class Options<T>
	{
		public T obj;
		public File file;
		public String url;
		public ImageView iv;
		public int placeholder=defaultPlaceholder;
		public int error=defaultError;
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
		if(options.obj instanceof Activity)
		{
			manager=Glide.with((Activity)options.obj);
		}
		else if(options.obj instanceof Fragment)
		{
			manager=Glide.with((Fragment)options.obj);
		}
		else if(options.obj instanceof android.support.v4.app.Fragment)
		{
			manager=Glide.with((android.support.v4.app.Fragment)options.obj);
		}
		else if(options.obj instanceof Context)
		{
			manager=Glide.with((Context)options.obj);
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

		if(!TextUtils.isEmpty(options.url))
		{
			manager.load(options.url).apply(reqOptions).into(options.iv);
		}
		else if(options.file!=null)
		{
			manager.load(options.file).apply(reqOptions).into(options.iv);
		}

		//manager.load(options.file).apply(reqOptions).into(options.iv);

		//manager.load(options.file).placeholder(options.placeholder).centerCrop().error(options.error).into(options
		// .iv);
	}
}