package app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;

import commons.image.ImageCompressor;

import com.x62.image.R;

import commons.utils.ResUtils;
import commons.utils.SysBarUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity
{
	private static final int ALBUM_OK=10;
	private int quality=85;
	private EditText etQuality;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SysBarUtils.statusBarTint(this,ResUtils.getColor(R.color.colorPrimary));
		etQuality=(EditText)findViewById(R.id.etQuality);
	}

	//	public void compressImage(View view)
	//	{
	//		Intent intent=new Intent(Intent.ACTION_PICK,null);
	//		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
	//		startActivityForResult(intent,ALBUM_OK);
	//	}

	//	@Override
	//	protected void onActivityResult(int requestCode,int resultCode,Intent data)
	//	{
	//		super.onActivityResult(requestCode,resultCode,data);
	//		if(requestCode==ALBUM_OK&&resultCode==Activity.RESULT_OK)
	//		{
	//			String[] filePathColumn={MediaStore.Images.Media.DATA};
	//			Cursor cursor=getContentResolver().query(data.getData(),filePathColumn,null,null,null);
	//			//MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
	//			cursor.moveToFirst();
	//			int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
	//			String picturePath=cursor.getString(columnIndex);
	//			cursor.close();
	//		}
	//	}

	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		super.onActivityResult(requestCode,resultCode,data);
		if(requestCode==ALBUM_OK&&resultCode==Activity.RESULT_OK)
		{
			String[] filePathColumn={MediaStore.Images.Media.DATA};
			Cursor cursor=getContentResolver().query(data.getData(),filePathColumn,null,null,null);
			// MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
			cursor.moveToFirst();
			int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
			String picturePath=cursor.getString(columnIndex);
			compress(picturePath);
			cursor.close();
		}
	}

	public void gotoContentLayout(View view)
	{
		Intent intent=new Intent(this,ContentActivity.class);
		startActivity(intent);
	}

	public void compressImage(View view)
	{
		String value=etQuality.getText().toString().trim();
		if(!TextUtils.isEmpty(value))
		{
			quality=Integer.parseInt(value);
		}
		//		Intent intent=new Intent(Intent.ACTION_PICK,null);
		//		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
		//		startActivityForResult(intent,ALBUM_OK);

		Intent intent=new Intent(this,PhotoPickActivity.class);
		startActivityForResult(intent,ALBUM_OK);
	}

	private void compress(String path)
	{
		// Bitmap bitmap=ImageCompressor.getBitmap(path);
		// System.out.println("占用内存->"+Formatter.formatFileSize(this,bitmap.getByteCount()));

		File file=new File(path);
		String sdRoot=Environment.getExternalStorageDirectory().getAbsolutePath();
		long ct=System.currentTimeMillis();
		File f1=new File(sdRoot,ct+"_1_java.jpg");
		File f2=new File(sdRoot,ct+"_2_java.webp");
		File f3=new File(sdRoot,ct+"_3_jni.jpg");

		ImageCompressor.javaCompressToJEPG(path,f1.getAbsolutePath());
		ImageCompressor.javaCompressToWEBP(path,f2.getAbsolutePath());
		//ImageCompressor.jniCompressToJEPG(path,f3.getAbsolutePath());
		System.out.println(file.getAbsolutePath());
		System.out.println("原始文件大小->"+Formatter.formatFileSize(this,file.length()));
		System.out.println("纯Java压缩JPG->"+Formatter.formatFileSize(this,f1.length()));
		System.out.println("纯Java压缩WEBP->"+Formatter.formatFileSize(this,f2.length()));
		//System.out.println("jni压缩JPG->"+Formatter.formatFileSize(this,f3.length()));
	}

	//	public void javaCompressToJEPG(String path,String to)
	//	{
	//		System.out.println("----------------纯Java压缩JPG-------------------");
	//		long start=System.currentTimeMillis();
	//
	//		try
	//		{
	//			Bitmap bitmap=ImageCompressor.getBitmap(path);
	//			FileOutputStream fos=new FileOutputStream(to);
	//			bitmap.compress(Bitmap.CompressFormat.JPEG,quality,fos);
	//			fos.close();
	//			bitmap.recycle();
	//			bitmap=null;
	//			Runtime.getRuntime().gc();
	//		}
	//		catch(Exception e)
	//		{
	//			e.printStackTrace();
	//		}
	//
	//		long end=System.currentTimeMillis();
	//		System.out.println("压缩耗时->"+(end-start));
	//		System.out.println("----------------纯Java压缩JPG-------------------");
	//	}
	//
	//	public void javaCompressToWEBP(String path,String to)
	//	{
	//		System.out.println("----------------纯Java压缩WEBP-------------------");
	//		long start=System.currentTimeMillis();
	//
	//		try
	//		{
	//			Bitmap bitmap=ImageCompressor.getBitmap(path);
	//			FileOutputStream fos=new FileOutputStream(to);
	//			bitmap.compress(Bitmap.CompressFormat.WEBP,quality,fos);
	//			fos.close();
	//			bitmap.recycle();
	//			bitmap=null;
	//			Runtime.getRuntime().gc();
	//		}
	//		catch(Exception e)
	//		{
	//			e.printStackTrace();
	//		}
	//		long end=System.currentTimeMillis();
	//		System.out.println("压缩耗时->"+(end-start));
	//		System.out.println("----------------纯Java压缩WEBP-------------------");
	//	}
	//
	//	public void jniCompressToJEPG(String path,String to)
	//	{
	//		System.out.println("----------------jni压缩JPG-------------------");
	//		long start=System.currentTimeMillis();
	//
	//		try
	//		{
	//			Bitmap bitmap=ImageCompressor.getBitmap(path);
	//			NativeUtil.compressBitmap(bitmap,quality,to,true);
	//			bitmap.recycle();
	//			bitmap=null;
	//			Runtime.getRuntime().gc();
	//		}
	//		catch(Exception e)
	//		{
	//			e.printStackTrace();
	//		}
	//
	//		long end=System.currentTimeMillis();
	//		System.out.println("压缩耗时->"+(end-start));
	//		System.out.println("----------------jni压缩JPG-------------------");
	//	}
}