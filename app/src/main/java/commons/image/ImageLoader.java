package commons.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
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

public class ImageLoader
{
	private static final File root=Environment.getExternalStorageDirectory();
	private static final Executor POOL=new ThreadPoolExecutor(0,1000,5L,TimeUnit.SECONDS,new SynchronousQueue<>());
	private static final SparseArray<Long> times=new SparseArray<>();

	private static final long MAX=1024*1024*32;
	private static long sum=0;

	private static Method cleaner;
	private static Method clean;

	static
	{
		try
		{
			Class<?> clazz=Class.forName("java.nio.DirectByteBuffer");
			cleaner=clazz.getDeclaredMethod("cleaner");
			if(cleaner!=null)
			{
				cleaner.setAccessible(true);
			}
			Log.e("xulei","cleaner->"+cleaner);

			clazz=Class.forName("sun.misc.Cleaner");
			clean=clazz.getDeclaredMethod("clean");
			if(clean!=null)
			{
				clean.setAccessible(true);
			}
			Log.e("xulei","cleaner->"+clean);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static String getCacheDir()
	{
		File file=new File(root,"ImageLoader");
		if(!file.exists())
		{
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}

	public static void load(ImageView view,String path,int placeholder)
	{
		int hashCode=view.hashCode();
		times.put(hashCode,System.currentTimeMillis());
		view.post(()->loadImage(view,path));
	}

	private static void loadImage(ImageView view,String path)
	{
		POOL.execute(()->
		{
			long start=System.currentTimeMillis();
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
				bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
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

				//MappedByteBuffer buffer=IOUtils.mapFile(path);
				bitmap=BitmapFactory.decodeFile(path,options);
				//view.setImageBitmap(bitmap);
			}
			long end=System.currentTimeMillis();
			Log.e("xulei","子线程耗时->"+(end-start));

			view.post(()->
			{
				Drawable drawable=view.getDrawable();
				if(drawable instanceof BitmapDrawable)
				{
					recycleBitmap(((BitmapDrawable)drawable).getBitmap());
				}
				view.setImageBitmap(bitmap);
				Log.e("xulei","耗时->"+view.hashCode()+","+(System.currentTimeMillis()-times.get(view.hashCode())));
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
					//Log.e("xulei","recycleBuffer->"+sum);
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
				//Log.e("xulei","recycleBitmap->"+sum);
				System.gc();
				sum=0;
			}
			//Log.e("xulei","recycleBitmap->recycle->"+bitmap.getWidth()+","+bitmap.getHeight()+","+bitmap.getByteCount());
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