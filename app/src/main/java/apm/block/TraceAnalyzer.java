package apm.block;

import java.io.File;

public class TraceAnalyzer implements Runnable
{
	private File file;
	private long duration;

	public TraceAnalyzer(String path,long duration)
	{
		this.file=new File(path+".trace");
		this.duration=duration;
	}

	@Override
	public void run()
	{
		if(duration<BlockMonitor.threshold)
		{
			file.delete();
		}
	}
}