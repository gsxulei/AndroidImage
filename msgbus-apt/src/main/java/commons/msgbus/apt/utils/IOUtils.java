package commons.msgbus.apt.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Flushable;

public class IOUtils
{
	private static void close(Closeable... closeables)
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

	public static String readFile(String path)
	{
		File file=new File(path);
		if(!file.exists())
		{
			return "";
		}
		StringBuilder result=new StringBuilder();
		BufferedReader reader=null;
		try
		{
			reader=new BufferedReader(new FileReader(file));
			String line;
			while((line=reader.readLine())!=null)
			{
				result.append(line);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(reader);
		}
		return result.toString();
	}

	public static void write(String path,String content)
	{
		FileWriter writer=null;
		try
		{
			writer=new FileWriter(path);
			writer.write(content);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally
		{
			close(writer);
		}
	}
}
