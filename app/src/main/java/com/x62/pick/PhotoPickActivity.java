package com.x62.pick;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.x62.app.base.ResUtils;
import com.x62.bean.PhotoAlbumBean;
import com.x62.image.ImagePreviewActivity;
import com.x62.image.R;
import com.x62.utils.ViewBind;

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

	//	@ViewBind.Bind(id=R.id.rvPhoto)
	//	private RecyclerView rvPhoto;

	@ViewBind.Bind(id=R.id.gvPhoto)
	private GridView gvPhoto;

	private PhotoListAdapter photoListAdapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_pick);
		ViewBind.bind(this);

		initData();

		photoListAdapter=new PhotoListAdapter(this);
		photoListAdapter.setData(list.get(0).photos);
		gvPhoto.setAdapter(photoListAdapter);

		gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
				String path=photoListAdapter.getItem(position);

				Intent intent=new Intent(getApplication(),ImagePreviewActivity.class);
				intent.putExtra("path",path);
				startActivity(intent);
			}
		});
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
		all.name=ResUtils.getString(R.string.all_photo);
		list.add(all);
		list.contains(all);

		Map<String,PhotoAlbumBean> map=new HashMap<>();
		while(mCursor.moveToNext())
		{
			//获取图片的路径
			String path=mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

			//获取该图片的父路径名
			//String parentName=new File(path).getParentFile().getName();
			File parent=new File(path).getParentFile();
			String parentPath=parent.getAbsolutePath();

			all.photos.add(path);

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