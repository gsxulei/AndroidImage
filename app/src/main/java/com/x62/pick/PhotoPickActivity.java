package com.x62.pick;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.x62.adapter.PhotoAlbumListAdapter;
import com.x62.commons.utils.ResUtils;
import com.x62.commons.base.BaseRecyclerViewAdapter;
import com.x62.bean.PhotoAlbumBean;
import com.x62.image.ImageData;
import com.x62.image.ImagePreviewActivity;
import com.x62.image.R;
import com.x62.commons.utils.SystemBarCompat;
import com.x62.commons.utils.ViewBind;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoPickActivity extends AppCompatActivity implements View.OnClickListener
{
	private List<PhotoAlbumBean> list=new ArrayList<>();

	@ViewBind.Bind(id=R.id.rvPhoto)
	private RecyclerView rvPhoto;

	//GridView会多次调用getView[position==0]方法,导致glide加载有问题
	//	@ViewBind.Bind(id=R.id.gvPhoto)
	//	private GridView gvPhoto;

	@ViewBind.Bind(id=R.id.lvAlbum)
	private ListView lvAlbum;

	@ViewBind.Bind(id=R.id.tvAlbumName)
	private TextView tvAlbumName;

	@ViewBind.Bind(id=R.id.vMask)
	private View vMask;

	private PhotoListAdapter photoListAdapter;

	private PhotoAlbumListAdapter photoAlbumListAdapter;

	private int currAlbumPosition=0;

	private Animation bottomEnter;
	private Animation bottomOut;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		setContentView(R.layout.activity_photo_pick);
		SystemBarCompat.tint(this,ResUtils.getColor(R.color.colorPrimaryDark));
		ViewBind.bind(this);

		initData();
		bottomEnter=AnimationUtils.loadAnimation(this,R.anim.bottom_enter);
		bottomOut=AnimationUtils.loadAnimation(this,R.anim.bottom_out);

		photoAlbumListAdapter=new PhotoAlbumListAdapter(this);
		photoAlbumListAdapter.addData(list);
		lvAlbum.setAdapter(photoAlbumListAdapter);
		lvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
				lvAlbum.setVisibility(View.GONE);
				vMask.setVisibility(View.GONE);
				lvAlbum.startAnimation(bottomOut);
				if(currAlbumPosition==position)
				{
					return;
				}
				photoListAdapter.setData(list.get(position).photos);
				tvAlbumName.setText(list.get(position).name);
				currAlbumPosition=position;
				photoAlbumListAdapter.setCurrAlbumPosition(currAlbumPosition);
			}
		});

		photoListAdapter=new PhotoListAdapter(this);
		photoListAdapter.setData(list.get(0).photos);

		GridLayoutManager manager=new GridLayoutManager(this,4);
		rvPhoto.setLayoutManager(manager);
		rvPhoto.setAdapter(photoListAdapter);
		photoListAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<String>()
		{
			@Override
			public void onItemClick(View view,int position,String path)
			{
				Intent intent=new Intent(getApplication(),ImagePreviewActivity.class);
				intent.putExtra("path",path);
				intent.putExtra("width",view.getWidth());
				intent.putExtra("height",view.getHeight());
				int[] location=new int[2];
				//view.getLocationOnScreen(location);
				view.getLocationInWindow(location);
				intent.putExtra("x",location[0]);
				intent.putExtra("y",location[1]);
				intent.putExtra("position",position);
				intent.putExtra("PhotoAlbum",list.get(currAlbumPosition));

				//intent传递字符串数组数据大小有限制
				//intent.putStringArrayListExtra("paths",photoListAdapter.getData());

				//				if(Build.VERSION.SDK_INT>21)
				//				{
				//					ActivityOptionsCompat options=ActivityOptionsCompat.makeScaleUpAnimation(view,
				// location[0],
				//							location[1],view.getWidth(),view.getHeight());
				//					startActivity(intent,options.toBundle());
				//					return;
				//				}
				startActivity(intent);
				//overridePendingTransition(0,0);
			}
		});

		//rvPhoto.setAdapter();
		//		gvPhoto.setAdapter(photoListAdapter);
		//
		//		gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener()
		//		{
		//			@Override
		//			public void onItemClick(AdapterView<?> parent,View view,int position,long id)
		//			{
		//				String path=photoListAdapter.getItem(position);
		//
		//				Intent intent=new Intent(getApplication(),ImagePreviewActivity.class);
		//				intent.putExtra("path",path);
		//				startActivity(intent);
		//			}
		//		});
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
		//list.contains(all);

		Map<String,PhotoAlbumBean> map=new HashMap<>();
		while(mCursor.moveToNext())
		{
			//获取图片的路径
			String path=mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

			if(!new File(path).exists())
			{
				continue;
			}

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
			bean.photos.add(path);
		}
		mCursor.close();

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
		ImageData.getInstance().setData(list);
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.tvAlbumName:
			{
				if(lvAlbum.getVisibility()==View.GONE)
				{
					lvAlbum.setVisibility(View.VISIBLE);
					vMask.setVisibility(View.VISIBLE);
					lvAlbum.startAnimation(bottomEnter);
				}
				else
				{
					lvAlbum.setVisibility(View.GONE);
					vMask.setVisibility(View.GONE);
					lvAlbum.startAnimation(bottomOut);
				}
			}
			break;
		}
	}

	@Override
	public void onBackPressed()
	{
		if(lvAlbum.getVisibility()==View.VISIBLE)
		{
			lvAlbum.setVisibility(View.GONE);
			vMask.setVisibility(View.GONE);
			lvAlbum.startAnimation(bottomOut);
			return;
		}
		super.onBackPressed();
	}
}