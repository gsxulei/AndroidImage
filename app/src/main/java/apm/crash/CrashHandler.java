package apm.crash;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import commons.utils.IOUtils;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * <p>
 * 需要在一个 Application 环境中让其运行
 */
public class CrashHandler implements UncaughtExceptionHandler
{
	// CrashHandler 实例
	private static CrashHandler INSTANCE=new CrashHandler();

	// 系统默认的 UncaughtException 处理类
	//private UncaughtExceptionHandler mDefaultHandler;

	// 用来存储设备信息
	private StringBuilder deviceInfo=new StringBuilder();

	// 用于格式化日期,作为日志文件名的一部分
	@SuppressLint("SimpleDateFormat")
	private DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	private String crashPath;

	/**
	 * 保证只有一个 CrashHandler 实例
	 */
	private CrashHandler()
	{
	}

	/**
	 * 获取 CrashHandler 实例 ,单例模式
	 */
	public static CrashHandler getInstance()
	{
		return INSTANCE;
	}

	/**
	 * 初始化
	 *
	 * @param context 上下文
	 */
	public void init(Context context,String path)
	{
		//mContext=context;

		// 获取系统默认的 UncaughtException 处理器
		//mDefaultHandler=Thread.getDefaultUncaughtExceptionHandler();

		// 设置该 CrashHandler 为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);

		if(TextUtils.isEmpty(path))
		{
			String sdRoot=Environment.getExternalStorageDirectory().getAbsolutePath();
			String appRootName=context.getPackageName();
			String appRoot=sdRoot+File.pathSeparator+appRootName;
			String crashPathName="crash";
			File crashFile=new File(appRoot,crashPathName);
			crashPath=crashFile.getAbsolutePath();
		}
		else
		{
			crashPath=path;
		}

		try
		{
			PackageManager pm=context.getPackageManager();
			PackageInfo pi=pm.getPackageInfo(context.getPackageName(),PackageManager.GET_ACTIVITIES);

			if(pi!=null)
			{
				String versionName="versionName="+(pi.versionName==null?"null":pi.versionName)+"\n";
				String versionCode="versionCode="+pi.versionCode+"\n";
				deviceInfo.append(versionName);
				deviceInfo.append(versionCode);
			}
		}
		catch(NameNotFoundException e)
		{
			e.printStackTrace();
		}

		Field[] fields=Build.class.getDeclaredFields();
		for(Field field : fields)
		{
			try
			{
				field.setAccessible(true);
				deviceInfo.append(field.getName());
				deviceInfo.append("=");
				deviceInfo.append(field.get(null).toString());
				deviceInfo.append("\n");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 当 UncaughtException 发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread,Throwable ex)
	{
		StringBuilder sb=new StringBuilder();
		sb.append(deviceInfo.toString());

		Writer writer=new StringWriter();
		PrintWriter printWriter=new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause=ex.getCause();
		while(cause!=null)
		{
			cause.printStackTrace(printWriter);
			cause=cause.getCause();
		}
		printWriter.close();

		String result=writer.toString();
		sb.append(result);

		long timestamp=System.currentTimeMillis();
		String time=formatter.format(new Date());
		String fileName="crash-"+time+"-"+timestamp+".txt";
		File file=new File(crashPath,fileName);
		IOUtils.write(file.getAbsolutePath(),sb.toString());

		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}
}