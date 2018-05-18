package com.x62.image;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.x62.bean.PhotoAlbumBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoPickActivity extends AppCompatActivity
{
	private List<PhotoAlbumBean> list=new ArrayList<>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		initData();
	}

	private void initData()
	{
		Uri mImageUri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		String MIME_TYPE=MediaStore.Images.Media.MIME_TYPE;
		ContentResolver mContentResolver=getContentResolver();

		//只查询jpeg和png的图片
		Cursor mCursor=mContentResolver.query(mImageUri,null,MIME_TYPE+"=? or "+MIME_TYPE+"=?",new
				String[]{"image/jpeg","image/png"},MediaStore.Images.Media.DATE_MODIFIED+" desc");

		if(mCursor==null)
		{
			return;
		}

		PhotoAlbumBean all=new PhotoAlbumBean();
		all.name="";

		list.add(all);

		Map<String,PhotoAlbumBean> map=new HashMap<>();
		while(mCursor.moveToNext())
		{
			//获取图片的路径
			String path=mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

			//获取该图片的父路径名
			//String parentName=new File(path).getParentFile().getName();
			File parent=new File(path).getParentFile();
			String parentPath=parent.getName();

			all.photos.add(path);


			//根据父路径名将图片放入到mGruopMap中
			//			if(!map.containsKey(parentPath))
			//			{
			//				PhotoAlbumBean bean=new PhotoAlbumBean();
			//				bean.name=parent.getName();
			//				bean.photos.add(parent.getAbsolutePath());
			//				list.add(bean);
			//			}
			//			else
			//			{
			//				map.get(parentPath).photos.add(parent.getAbsolutePath());
			//			}

			PhotoAlbumBean bean=map.get(parentPath);
			if(bean==null)
			{
				bean=new PhotoAlbumBean();
				bean.name=parent.getName();
				map.put(parentPath,bean);
				list.add(bean);
			}
			bean.photos.add(parent.getAbsolutePath());
		}

		Collections.sort(list,new Comparator<PhotoAlbumBean>()
		{
			@Override
			public int compare(PhotoAlbumBean pab1,PhotoAlbumBean pab2)
			{
				try
				{
					return pab2.photos.size()-pab1.photos.size();
				}
				catch(Exception e)
				{
					return 0;
				}
			}
		});
	}
}