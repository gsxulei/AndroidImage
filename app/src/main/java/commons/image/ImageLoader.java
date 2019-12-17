package commons.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 图片加载器
 */
public class ImageLoader
{
	private static final File root=Environment.getExternalStorageDirectory();
	private static final Executor POOL=new ThreadPoolExecutor(0,1000,5L,TimeUnit.SECONDS,new SynchronousQueue<>());
	/**
	 * GC阈值
	 */
	private static final long MAX=1024*1024*32;

	/**
	 * 当前待回收内存大小
	 */
	private static long sum=0;

	private static Method cleaner;
	private static Method clean;

	static
	{
		//Android7.0及以后的版本,DirectByteBuffer可以手动释放内存
		//但api为隐藏,需要使用反射
		try
		{
			Class<?> clazz=Class.forName("java.nio.DirectByteBuffer");
			cleaner=clazz.getDeclaredMethod("cleaner");
			if(cleaner!=null)
			{
				cleaner.setAccessible(true);
			}

			clazz=Class.forName("sun.misc.Cleaner");
			clean=clazz.getDeclaredMethod("clean");
			if(clean!=null)
			{
				clean.setAccessible(true);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static String getCacheDir()
	{
		File imageLoader=new File(root,"ImageLoader");
		File file=new File(imageLoader,"Cache");
		if(!file.exists())
		{
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}

	public static void load(ImageView view,String path)
	{
		int hashCode=view.hashCode();
		if(view.getWidth()>0&&view.getHeight()>0)
		{
			loadImage(view,path);
			return;
		}
		//view.post(()->loadImage(view,path));
		view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				loadImage(view,path);
			}
		});
	}

	private static void loadImage(ImageView view,String path)
	{
		POOL.execute(()->
		{
			int imageW=view.getWidth();
			int imageH=view.getHeight();

			int name=(path+"-"+imageW+"-"+imageH).hashCode();
			File cacheFile=new File(getCacheDir(),name+"");
			Bitmap bitmap;
			if(cacheFile.exists())
			{
				MappedByteBuffer buffer=mapFile(cacheFile.getAbsolutePath());
				int width=buffer.getInt();
				int height=buffer.getInt();
				int size=(buffer.capacity()-8)/width/height;
				Bitmap.Config config=Bitmap.Config.RGB_565;
				if(size==4)
				{
					config=Bitmap.Config.ARGB_8888;
				}
				else if(size==1)
				{
					config=Bitmap.Config.ALPHA_8;
				}
				bitmap=Bitmap.createBitmap(width,height,config);
				bitmap.copyPixelsFromBuffer(buffer);
				buffer.clear();
				recycleBuffer(buffer);
			}
			else
			{
				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inJustDecodeBounds=true;
				BitmapFactory.decodeFile(path,options);
				int width=options.outWidth;
				int height=options.outHeight;

				int inSampleSizeW=width/imageW;
				int inSampleSizeH=height/imageH;
				options.inSampleSize=inSampleSizeW>inSampleSizeH?inSampleSizeW:inSampleSizeH;
				options.inJustDecodeBounds=false;
				options.inPreferredConfig=Bitmap.Config.RGB_565;

				//MappedByteBuffer buffer=IOUtils.mapFile(path);
				bitmap=BitmapFactory.decodeFile(path,options);
			}

			view.post(()->
			{
				Drawable drawable=view.getDrawable();
				if(drawable instanceof BitmapDrawable)
				{
					recycleBitmap(((BitmapDrawable)drawable).getBitmap());
				}
				view.setImageBitmap(bitmap);
			});

			if(!cacheFile.exists())
			{
				if(bitmap==null)
				{
					return;
				}
				ByteBuffer buffer=ByteBuffer.allocate(bitmap.getByteCount());
				int w=bitmap.getWidth();
				int h=bitmap.getHeight();
				bitmap.copyPixelsToBuffer(buffer);
				writeBitmap(cacheFile.getAbsolutePath(),w,h,buffer.array());
				buffer.clear();
			}
		});
	}

	private static void recycleBuffer(ByteBuffer buffer)
	{
		if(cleaner==null||clean==null)
		{
			return;
		}
		POOL.execute(()->
		{
			try
			{
				sum+=buffer.capacity();
				clean.invoke(cleaner.invoke(buffer));
				if(sum>MAX)
				{
					System.gc();
					sum=0;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	private static void recycleBitmap(Bitmap bitmap)
	{
		if(bitmap==null)
		{
			return;
		}

		POOL.execute(()->
		{
			sum+=bitmap.getByteCount();
			bitmap.recycle();

			if(sum>MAX)
			{
				System.gc();
				sum=0;
			}
		});
	}

	private static MappedByteBuffer mapFile(String path)
	{
		File file=new File(path);
		return mapFile(path,file.length());
	}

	private static MappedByteBuffer mapFile(String path,long size)
	{
		RandomAccessFile accessFile=null;
		FileChannel channel=null;
		MappedByteBuffer buffer=null;
		try
		{
			accessFile=new RandomAccessFile(path,"rw");
			channel=accessFile.getChannel();
			buffer=channel.map(FileChannel.MapMode.READ_WRITE,0,size).load();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(accessFile,channel);
		}
		return buffer;
	}

	private static void writeBitmap(String path,int width,int height,byte[] data)
	{
		FileOutputStream fos=null;
		DataOutputStream dos=null;
		try
		{
			fos=new FileOutputStream(path);
			dos=new DataOutputStream(fos);
			dos.writeInt(width);
			dos.writeInt(height);
			dos.write(data);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(fos,dos);
		}
	}

	public static void close(Closeable... closeables)
	{
		if(closeables==null)
		{
			return;
		}

		for(Closeable closeable : closeables)
		{
			if(closeable==null)
			{
				continue;
			}

			try
			{
				if(closeable instanceof Flushable)
				{
					((Flushable)closeable).flush();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				closeable.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}