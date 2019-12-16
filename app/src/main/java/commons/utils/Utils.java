package commons.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.List;

public class Utils
{
	public static String digest(byte[] bytes,String algorithm)
	{
		try
		{
			MessageDigest md=MessageDigest.getInstance(algorithm);
			byte[] result=md.digest(bytes);
			StringBuilder buffer=new StringBuilder();
			for(byte b : result)
			{
				int number=b&0xff;
				String str=Integer.toHexString(number);
				if(str.length()==1)
				{
					buffer.append("0");
				}
				buffer.append(str);
			}
			return buffer.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	public static String digest(InputStream is,String algorithm)
	{
		MessageDigest digest;
		byte[] buffer=new byte[1024];
		int len;
		try
		{
			digest=MessageDigest.getInstance("MD5");
			while((len=is.read(buffer,0,1024))!=-1)
			{
				digest.update(buffer,0,len);
			}

			byte[] result=digest.digest();
			StringBuilder buff=new StringBuilder();
			for(byte b : result)
			{
				int number=b&0xff;
				String str=Integer.toHexString(number);
				if(str.length()==1)
				{
					buff.append("0");
				}
				buff.append(str);
			}
			return buff.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
		finally
		{
			IOUtils.close(is);
		}
	}

	public static String md5(InputStream is)
	{
		return digest(is,"md5");
	}

	public static String md5(File file)
	{
		try
		{
			return digest(new FileInputStream(file),"md5");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	public static String md5(byte[] bytes)
	{
		return digest(bytes,"md5");
	}

	/**
	 * 返回32位16进制(128位bit)
	 *
	 * @param text
	 * @return
	 */
	public static String md5(String text)
	{
		if(text==null||text.length()==0)
		{
			return "";
		}
		return md5(text.getBytes());
	}

	public static String hash1(byte[] bytes)
	{
		return digest(bytes,"SHA1");
	}

	/**
	 * 返回40位16进制(160位bit)
	 *
	 * @param text
	 * @return
	 */
	public static String hash1(String text)
	{
		if(text==null||text.length()==0)
		{
			return "";
		}
		return hash1(text.getBytes());
	}

	public static String hash256(byte[] bytes)
	{
		return digest(bytes,"SHA-256");
	}

	/**
	 * 返回64位16进制(256位bit)
	 *
	 * @param text
	 * @return
	 */
	public static String hash256(String text)
	{
		if(text==null||text.length()==0)
		{
			return "";
		}
		return hash256(text.getBytes());
	}

	/**
	 * 判断字符串是否为空
	 *
	 * @param str 待判断字符串
	 * @return true-字符串为空,false-字符串不为空
	 */
	public static boolean isEmpty(String str)
	{
		return str==null||str.length()==0;
	}

	/**
	 * 判断字符串是否不为空
	 *
	 * @param str 待判断字符串
	 * @return true-字符串不为空,false-字符串为空
	 */
	public static boolean notEmpty(String str)
	{
		return !isEmpty(str);
	}

	public static String getStackTrace(Throwable throwable)
	{
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		try
		{
			throwable.printStackTrace(pw);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			IOUtils.close(pw);
		}
		return sw.toString();
	}


	public static String getProcessName(Context context)
	{
		ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		if(activityManager==null)
		{
			return null;
		}
		List<ActivityManager.RunningAppProcessInfo> appProcesses=activityManager.getRunningAppProcesses();

		int myPid=Process.myPid();

		if(appProcesses==null||appProcesses.size()==0)
		{
			return null;
		}

		for(ActivityManager.RunningAppProcessInfo appProcess : appProcesses)
		{
			if(appProcess.processName.equals(context.getPackageName()))
			{
				if(appProcess.pid==myPid)
				{
					return appProcess.processName;
				}
			}
		}
		return null;
	}
}