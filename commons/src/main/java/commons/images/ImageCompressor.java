package commons.images;

import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import net.bither.util.NativeUtil;

/**
 * 图片压缩
 */
public class ImageCompressor
{
	// public final static int QUALITY_320P=320;// 480, 320
	// public final static int QUALITY_360P=360;// 640, 360
	// public final static int QUALITY_480P=480;// 640, 480
	// public final static int QUALITY_720P=720;// 1280, 720
	// public final static int QUALITY_1080P=1080;// 1920, 1080
	// public final static int QUALITY_2K=1440;// 2560, 1440
	// public final static int QUALITY_4K=2160;// 3840, 2160

	private static int quality=70;

	/**
	 * 获取图片的旋转角度
	 *
	 * @param path 图片路径
	 * @return 图片的旋转角度
	 */
	private static int getDegree(String path)
	{
		int degree=0;
		try
		{
			ExifInterface exifInterface=new ExifInterface(path);
			int orientation=exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface
					.ORIENTATION_NORMAL);
			switch(orientation)
			{
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree=90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree=180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree=270;
					break;
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return degree;
	}

	private static float getScale(int width,int height,int max)
	{
		float scale;
		if(height<width)
		{
			scale=((float)max)/height;
		}
		else
		{
			scale=((float)max)/width;
		}
		if(scale>1)
		{
			scale=1.0F;
		}
		return scale;
	}

	public static Bitmap getBitmap(String path)
	{
		System.out.println("----------------图片解码-------------------");
		int max=1080;
		long start=System.currentTimeMillis();
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(path,options);
		int width=options.outWidth;
		int height=options.outHeight;
		int degree=getDegree(path);

		System.out.println("原始w->"+width);
		System.out.println("原始h->"+height);
		System.out.println("degree->"+degree);

		float scale=getScale(width,height,max);

		options.inSampleSize=(int)scale;
		options.inJustDecodeBounds=false;
		Bitmap bitmap=BitmapFactory.decodeFile(path,options);

		// 创建操作图片用的matrix对象
		Matrix matrix=new Matrix();
		// 缩放图片动作
		if(scale<1)
		{
			matrix.postScale(scale,scale);
		}
		if(degree>0)
		{
			matrix.postRotate(degree);
		}
		Bitmap result=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);

		//如果新尺寸与原始尺寸一样则返回原bitmap
		if(result!=bitmap)
		{
			bitmap.recycle();
			Runtime.getRuntime().gc();
		}

		System.out.println("处理后w->"+result.getWidth());
		System.out.println("处理后h->"+result.getHeight());

		long end=System.currentTimeMillis();
		System.out.println("解码耗时->"+(end-start));
		System.out.println("getConfig->"+result.getConfig().name());
		System.out.println("----------------图片解码-------------------");
		return result;
	}

	/**
	 * Java层压缩图片成JPG格式
	 *
	 * @param path 图片原路径
	 * @param to   图片压缩后路径
	 */
	public static void javaCompressToJEPG(String path,String to)
	{
		System.out.println("----------------纯Java压缩JPG-------------------");
		long start=System.currentTimeMillis();

		try
		{
			Bitmap bitmap=ImageCompressor.getBitmap(path);
			FileOutputStream fos=new FileOutputStream(to);
			bitmap.compress(Bitmap.CompressFormat.JPEG,quality,fos);
			fos.close();
			bitmap.recycle();
			bitmap=null;
			Runtime.getRuntime().gc();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		long end=System.currentTimeMillis();
		System.out.println("压缩耗时->"+(end-start));
		System.out.println("----------------纯Java压缩JPG-------------------");
	}

	public static void javaCompressToWEBP(String path,String to)
	{
		System.out.println("----------------纯Java压缩WEBP-------------------");
		long start=System.currentTimeMillis();

		try
		{
			Bitmap bitmap=ImageCompressor.getBitmap(path);
			FileOutputStream fos=new FileOutputStream(to);
			bitmap.compress(Bitmap.CompressFormat.WEBP,quality,fos);
			fos.close();
			bitmap.recycle();
			bitmap=null;
			Runtime.getRuntime().gc();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		long end=System.currentTimeMillis();
		System.out.println("压缩耗时->"+(end-start));
		System.out.println("----------------纯Java压缩WEBP-------------------");
	}

	public static void jniCompressToJEPG(String path,String to)
	{
		System.out.println("----------------jni压缩JPG-------------------");
		long start=System.currentTimeMillis();

		try
		{
			Bitmap bitmap=ImageCompressor.getBitmap(path);
			NativeUtil.compressBitmap(bitmap,quality,to,true);
			bitmap.recycle();
			bitmap=null;
			Runtime.getRuntime().gc();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		long end=System.currentTimeMillis();
		System.out.println("压缩耗时->"+(end-start));
		System.out.println("----------------jni压缩JPG-------------------");
	}
}