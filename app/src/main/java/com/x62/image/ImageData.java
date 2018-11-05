package com.x62.image;

import com.x62.bean.PhotoAlbumBean;

import java.util.ArrayList;
import java.util.List;

public class ImageData
{
	private List<PhotoAlbumBean> data=new ArrayList<>();

	private static class Loader
	{
		private static final ImageData INSTANCE=new ImageData();
	}

	private ImageData()
	{
	}

	public static ImageData getInstance()
	{
		return Loader.INSTANCE;
	}

	public void setData(List<PhotoAlbumBean> data)
	{
		this.data=data;
	}

	public List<PhotoAlbumBean> getData()
	{
		return data;
	}
}