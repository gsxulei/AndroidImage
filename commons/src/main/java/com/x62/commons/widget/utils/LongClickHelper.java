package com.x62.commons.widget.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 长按连续执行
 */
public class LongClickHelper implements View.OnTouchListener
{
	private Runnable runnable;

	//private long lastClickTime;

	private Timer timer;

	private Handler handler=new Handler(Looper.getMainLooper());

	private int bgId=0;
	private int bgClickId=0;


	public void setView(View view,Runnable runnable)
	{
		setView(view,runnable,0,0);
	}

	/**
	 * 设置View
	 *
	 * @param view      待处理的View
	 * @param runnable  长按执行回调
	 * @param bgId      View默认背景
	 * @param bgClickId View按下背景
	 */
	public void setView(View view,Runnable runnable,int bgId,int bgClickId)
	{
		if(view==null)
		{
			return;
		}
		view.setOnTouchListener(this);
		this.runnable=runnable;
		this.bgId=bgId;
		this.bgClickId=bgClickId;
	}

	@Override
	public boolean onTouch(View v,MotionEvent event)
	{
		//boolean isLongClick=false;

		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			{
				if(bgClickId>0)
				{
					v.setBackgroundResource(bgClickId);
				}

				//lastClickTime=System.currentTimeMillis();
				timer=new Timer();

				TimerTask timerTask=new TimerTask()
				{
					@Override
					public void run()
					{
						handler.post(runnable);
					}
				};
				timer.schedule(timerTask,10,200);
				//handler.postDelayed(runnable,300);
			}
			break;

			case MotionEvent.ACTION_UP:
			{
				//				if(lastClickTime-System.currentTimeMillis()<300)
				//				{
				//					//isLongClick=true;
				//					//handler.removeCallbacks(runnable);
				//					//v.performClick();
				//				}
				//				else
				//				{
				//					//handler.removeCallbacks(runnable);
				//				}
				if(bgId>0)
				{
					v.setBackgroundResource(bgId);
				}
				if(timer!=null)
				{
					timer.cancel();
					timer.purge();
					timer=null;
				}
			}
			break;
		}
		return true;
	}
}