package image;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import commons.app.base.AppBase;

/**
 * 图片查询
 */
public class ImageQuery
{
	public static List<PhotoAlbumBean> queryAlbum()
	{
		List<PhotoAlbumBean> list=new ArrayList<>();
		Context context=AppBase.getInstance().getContext();
		Uri mImageUri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		ContentResolver mContentResolver=context.getContentResolver();

		final String DATA=MediaStore.Images.Media.DATA;
		final String BUCKET_ID=MediaStore.Images.Media.BUCKET_ID;
		final String BUCKET_DISPLAY_NAME=MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
		final String DATE_MODIFIED=MediaStore.Images.Media.DATE_MODIFIED;
		final String MIME_TYPE=MediaStore.Images.Media.MIME_TYPE;

		//只查询jpeg和png的图片
		String[] projection=new String[]{DATA,BUCKET_ID,BUCKET_DISPLAY_NAME,DATE_MODIFIED,"count(_id)"};
		String selection=MIME_TYPE+"=? or "+MIME_TYPE+"=?) group by ("+BUCKET_ID;
		String[] selectionArgs=new String[]{"image/jpeg","image/png"};
		String sortOrder=DATE_MODIFIED+" desc";
		Cursor mCursor=mContentResolver.query(mImageUri,projection,selection,selectionArgs,sortOrder);

		if(mCursor==null)
		{
			return list;
		}

		PhotoAlbumBean all=new PhotoAlbumBean();
		list.add(all);
		try
		{
			while(mCursor.moveToNext())
			{
				//获取图片的路径
				String path=mCursor.getString(0);

				if(!new File(path).exists())
				{
					continue;
				}

				String id=mCursor.getString(1);
				String name=mCursor.getString(2);
				String lastModified=mCursor.getInt(3)+1000+"";
				int size=mCursor.getInt(4);

				PhotoAlbumBean bean=new PhotoAlbumBean();
				bean.id=id;
				bean.name=name;
				bean.cover=path;
				bean.size=size;
				bean.lastModified=lastModified;

				list.add(bean);

				all.size+=size;
				if(TextUtils.isEmpty(all.cover))
				{
					all.cover=path;
					all.lastModified=lastModified;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			mCursor.close();
		}

		Collections.sort(list,(pab1,pab2)->
		{
			try
			{
				return pab2.size-pab1.size;
			}
			catch(Exception e)
			{
				return 0;
			}
		});

		return list;
	}

	/**
	 * 查询某一相册(目录)的图片
	 *
	 * @param id           相册ID
	 * @param lastModified 最后更新时间
	 */
	public static PhotoAlbumBean queryImageByAlbum(String id,String lastModified)
	{
		Context context=AppBase.getInstance().getContext();
		Uri mImageUri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		ContentResolver mContentResolver=context.getContentResolver();

		final String DATA=MediaStore.Images.Media.DATA;
		final String DATE_MODIFIED=MediaStore.Images.Media.DATE_MODIFIED;
		final String MIME_TYPE=MediaStore.Images.Media.MIME_TYPE;

		//只查询jpeg和png的图片
		String[] projection=new String[]{DATA,DATE_MODIFIED};
		String selection="("+MIME_TYPE+"=? or "+MIME_TYPE+"=?) and "+DATE_MODIFIED+"< ?";
		String[] selectionArgs=new String[]{"image/jpeg","image/png",lastModified};
		if(!TextUtils.isEmpty(id))
		{
			selection+="and bucket_id=?";
			selectionArgs=new String[]{"image/jpeg","image/png",lastModified,id};
		}
		String sortOrder=DATE_MODIFIED+" desc limit 200";
		Cursor mCursor=mContentResolver.query(mImageUri,projection,selection,selectionArgs,sortOrder);

		PhotoAlbumBean bean=new PhotoAlbumBean();
		if(mCursor==null)
		{
			return bean;
		}

		try
		{
			while(mCursor.moveToNext())
			{
				//获取图片的路径
				String path=mCursor.getString(0);

				if(!new File(path).exists())
				{
					continue;
				}
				bean.photos.add(path);
				bean.lastModified=mCursor.getString(1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			mCursor.close();
		}

		return bean;
	}
}