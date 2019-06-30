package app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import commons.agent.BaseActivity;
import commons.msgbus.MsgBus;
import commons.msgbus.MsgEvent;
import commons.msgbus.MsgReceiver;

import image.ImageModel;
import image.PhotoAlbumListAdapter;

import commons.utils.ResUtils;
import commons.base.BaseRecyclerViewAdapter;

import image.PhotoAlbumBean;

import com.x62.image.R;

import commons.utils.SystemBarCompat;
import commons.utils.ViewBind;
import image.PhotoListAdapter;

import commons.utils.MsgEventId;

import java.util.ArrayList;
import java.util.List;

public class PhotoPickActivity extends BaseActivity implements View.OnClickListener
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

	private boolean isLoading=false;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		setContentView(R.layout.activity_photo_pick);
		SystemBarCompat.tint(this,ResUtils.getColor(R.color.colorPrimaryDark));
		ViewBind.bind(this);

		//initData();
		bottomEnter=AnimationUtils.loadAnimation(mContext,R.anim.bottom_enter);
		bottomOut=AnimationUtils.loadAnimation(mContext,R.anim.bottom_out);

		photoAlbumListAdapter=new PhotoAlbumListAdapter(this);
		photoAlbumListAdapter.addData(list);
		lvAlbum.setAdapter(photoAlbumListAdapter);
		rvPhoto.addOnScrollListener(new RecyclerView.OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,int newState)
			{
				super.onScrollStateChanged(recyclerView,newState);
				if(newState==0&&!recyclerView.canScrollVertically(1))
				{
					loadPhoto();
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView,int dx,int dy)
			{
				super.onScrolled(recyclerView,dx,dy);
			}
		});

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
				//				photoListAdapter.setData(list.get(position).photos);
				//				tvAlbumName.setText(list.get(position).name);
				//				currAlbumPosition=position;
				//				photoAlbumListAdapter.setCurrAlbumPosition(currAlbumPosition);
				tvAlbumName.setText(list.get(position).name);
				currAlbumPosition=position;
				photoAlbumListAdapter.setCurrAlbumPosition(currAlbumPosition);
				PhotoAlbumBean bean=list.get(currAlbumPosition);
				if(bean.photos.size()<=0)
				{
					loadPhoto();
					return;
				}
				photoListAdapter.setData(bean.photos);
			}
		});

		photoListAdapter=new PhotoListAdapter(mContext);
		//photoListAdapter.setData(list.get(0).photos);

		GridLayoutManager manager=new GridLayoutManager(this,4);
		rvPhoto.setLayoutManager(manager);
		rvPhoto.setAdapter(photoListAdapter);
		photoListAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<String>()
		{
			@Override
			public void onItemClick(View view,int position,String path)
			{
				MsgEvent<List<String>> event=new MsgEvent<>();
				event.id=MsgEventId.ID_100002;
				event.t=list.get(currAlbumPosition).photos;
				MsgBus.sendSticky(event);

				Bundle bundle=new Bundle();
				bundle.putInt("position",position);
				open(ImagePreviewActivity.class,bundle);

				//				Intent intent=new Intent(getApplication(),ImagePreviewActivity.class);
				//				intent.putExtra("width",view.getWidth());
				//				intent.putExtra("height",view.getHeight());
				//				int[] location=new int[2];
				//				//view.getLocationOnScreen(location);
				//				view.getLocationInWindow(location);
				//				intent.putExtra("x",location[0]);
				//				intent.putExtra("y",location[1]);
				//				intent.putExtra("position",position);
				//				startActivity(intent);

				//				Intent intent=new Intent(getApplication(),ImagePreviewActivity.class);
				//				intent.putExtra("path",path);
				//				intent.putExtra("width",view.getWidth());
				//				intent.putExtra("height",view.getHeight());
				//				int[] location=new int[2];
				//				//view.getLocationOnScreen(location);
				//				view.getLocationInWindow(location);
				//				intent.putExtra("x",location[0]);
				//				intent.putExtra("y",location[1]);
				//				intent.putExtra("position",position);
				//				intent.putExtra("PhotoAlbum",list.get(currAlbumPosition));

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
				//startActivity(intent);
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

		MsgBus.register(ImageModel.class);
		MsgBus.send(MsgEventId.ID_200010);

		showLoading();
		//		loadingDialog=new LoadingDialog(this);
		//		loadingDialog.setLoadingText("请稍候...");
		//		loadingDialog.show();
	}


	/**
	 * 查询相册成功
	 *
	 * @param event 含有相册数据的消息
	 */
	@MsgReceiver(id=MsgEventId.ID_200011)
	private void onAlbumDataQuerySuccess(MsgEvent<List<PhotoAlbumBean>> event)
	{
		list.addAll(event.t);
		photoAlbumListAdapter.addData(list);
		loadPhoto();
	}

	private void loadPhoto()
	{
		PhotoAlbumBean bean=list.get(currAlbumPosition);
		if(bean.isLoadMore())
		{
			MsgBus.register(ImageModel.class);
			MsgEvent<String[]> event=new MsgEvent<>();
			event.id=MsgEventId.ID_200020;
			event.t=new String[]{bean.id,bean.lastModified};
			MsgBus.send(event);
			isLoading=true;
		}
	}

	/**
	 * 查询图片成功
	 *
	 * @param event 含有图片数据的消息
	 */
	@MsgReceiver(id=MsgEventId.ID_200021)
	private void onImageDataQuerySuccess(MsgEvent<PhotoAlbumBean> event)
	{
		hideLoading();
		PhotoAlbumBean bean=list.get(currAlbumPosition);
		//bean.lastModified=event.t.lastModified;
		if(!TextUtils.isEmpty(event.t.lastModified))
		{
			bean.lastModified=event.t.lastModified;
		}
		bean.photos.addAll(event.t.photos);
		photoListAdapter.setData(bean.photos);
		isLoading=false;
	}

	/**
	 * 查询图片失败
	 *
	 * @param event 无用
	 */
	@MsgReceiver(id=MsgEventId.ID_200022)
	void onImageDataQueryFail(MsgEvent<String> event)
	{
		hideLoading();
		isLoading=false;
	}

	//	private void initData()
	//	{
	//		Uri mImageUri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	//		String MIME_TYPE=MediaStore.Images.Media.MIME_TYPE;
	//		ContentResolver mContentResolver=getContentResolver();
	//
	//		//只查询jpeg和png的图片
	//		Cursor mCursor=mContentResolver.query(mImageUri,null,MIME_TYPE+"=? or "+MIME_TYPE+"=?",new
	//				String[]{"image/jpeg","image/png"},MediaStore.Images.Media.DATE_MODIFIED+" desc");
	//
	//		if(mCursor==null)
	//		{
	//			return;
	//		}
	//
	//		PhotoAlbumBean all=new PhotoAlbumBean();
	//		all.name=ResUtils.getString(R.string.all_photo);
	//		list.add(all);
	//		//list.contains(all);
	//
	//		Map<String,PhotoAlbumBean> map=new HashMap<>();
	//		while(mCursor.moveToNext())
	//		{
	//			//获取图片的路径
	//			String path=mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
	//
	//			if(!new File(path).exists())
	//			{
	//				continue;
	//			}
	//
	//			//获取该图片的父路径名
	//			//String parentName=new File(path).getParentFile().getName();
	//			File parent=new File(path).getParentFile();
	//			String parentPath=parent.getAbsolutePath();
	//
	//			all.photos.add(path);
	//
	//			PhotoAlbumBean bean=map.get(parentPath);
	//			if(bean==null)
	//			{
	//				bean=new PhotoAlbumBean();
	//				bean.name=parent.getName();
	//				map.put(parentPath,bean);
	//				list.add(bean);
	//			}
	//			bean.photos.add(path);
	//		}
	//		mCursor.close();
	//
	//		Collections.sort(list,new Comparator<PhotoAlbumBean>()
	//		{
	//			@Override
	//			public int compare(PhotoAlbumBean pab1,PhotoAlbumBean pab2)
	//			{
	//				try
	//				{
	//					return pab2.photos.size()-pab1.photos.size();
	//				}
	//				catch(Exception e)
	//				{
	//					return 0;
	//				}
	//			}
	//		});
	//		ImageData.getInstance().setData(list);
	//	}

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