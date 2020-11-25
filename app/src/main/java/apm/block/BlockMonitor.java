package apm.block;

import android.os.Debug;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 卡段监控
 */
public class BlockMonitor implements Printer
{
	private static final ExecutorService POOL=Executors.newCachedThreadPool();
	private static String tracePath;
	private static int intervalUs;
	public static long threshold=1000;
	private long lastTime;

	public static void init()
	{
		long startTime=System.nanoTime();
		Looper.getMainLooper().setMessageLogging(new BlockMonitor());

		File root=Environment.getExternalStorageDirectory();
		File traceDir=new File(root,"trace");
		if(!traceDir.exists())
		{
			traceDir.mkdirs();
		}
		tracePath=traceDir.getAbsolutePath()+"/";
		long endTime=System.nanoTime();
		intervalUs=(int)(endTime-startTime)/10000;
		if(intervalUs<10)
		{
			intervalUs=10;
		}
		Log.e("trace","---------"+intervalUs);

	}

	@Override
	public void println(String x)
	{
		if(x.charAt(0)=='>')
		{
			if(lastTime!=0)
			{
				Debug.stopMethodTracing();
				long duration=System.currentTimeMillis()-lastTime;
				String traceName=tracePath+lastTime;
				POOL.execute(new TraceAnalyzer(traceName,duration));
			}
			lastTime=System.currentTimeMillis();
			Log.e("trace",lastTime+"|"+x);
			String traceName=tracePath+lastTime;
			Debug.startMethodTracingSampling(traceName,0,intervalUs);
		}
		else
		{
			Debug.stopMethodTracing();
			long duration=System.currentTimeMillis()-lastTime;
			Log.e("trace",lastTime+"|"+(duration)+"|"+x);
			String traceName=tracePath+lastTime;
			POOL.execute(new TraceAnalyzer(traceName,duration));
			lastTime=0;
		}
	}
}