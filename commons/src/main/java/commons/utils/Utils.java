package commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

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
		byte buffer[]=new byte[1024];
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
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str)
	{
		return str==null||str.length()==0;
	}

	/**
	 * 判断字符串是否不为空
	 *
	 * @param str
	 * @return
	 */
	public static boolean notEmpty(String str)
	{
		return !isEmpty(str);
	}
}