package apm.trace;

import android.os.Looper;
import android.os.SystemClock;

import java.util.Arrays;

public class StackRecord extends Thread
{
	private boolean mRun;
	private boolean mRecord;
	private long intervalUs=2;

	public StackRecord()
	{
		mRun=true;
	}

	@Override
	public void run()
	{
		while(mRun)
		{
			StackTraceElement[] elements=Looper.getMainLooper().getThread().getStackTrace();
			if(mRecord)
			{
				StackTraceElement[] traces=Arrays.copyOf(elements,elements.length);
				TraceView.add(traces,System.currentTimeMillis());
			}
			SystemClock.sleep(intervalUs);
		}
	}

	public void startRecord()
	{
		mRecord=true;
	}

	public void stopRecord()
	{
		mRecord=false;
	}

	public void setIntervalUs(long intervalUs)
	{
		this.intervalUs=intervalUs;
	}
}