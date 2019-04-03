package xplugin.utils;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;

public class Utils
{
	public static void copy(String from,String to)
	{
		FileInputStream fis=null;
		FileOutputStream fos=null;
		try
		{
			fis=new FileInputStream(from);
			fos=new FileOutputStream(to);
			byte[] buffer=new byte[1024*10];
			int len=0;
			while((len=fis.read(buffer))>0)
			{
				fos.write(buffer,0,len);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(fis,fos);
		}
	}

	/**
	 * 关闭流
	 * 
	 * @param closeables
	 */
	public static void close(Closeable...closeables)
	{
		if(closeables==null)
		{
			return;
		}

		for(Closeable closeable:closeables)
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