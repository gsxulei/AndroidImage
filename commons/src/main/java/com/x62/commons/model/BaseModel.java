package com.x62.commons.model;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基本数据处理封装
 * 
 * @author GSXL
 *
 * @param <P>
 *            参数
 * @param <S>
 *            处理成功回调参数
 * @param <F>
 *            处理失败回调参数
 */
public abstract class BaseModel<P,S,F>
{
	private final int SUCCESS=0;
	private final int FAIL=1;

	private Map<String,ModelCallBack<S,F>> cbs=new HashMap<>();
	private static ExecutorService es=Executors.newCachedThreadPool();
	private Map<String,S> ss=new HashMap<>();
	private Map<String,F> fs=new HashMap<>();

	private Handler handler=new Handler(Looper.getMainLooper())
	{
		public void handleMessage(Message msg)
		{
			String key=""+msg.arg1;
			ModelCallBack<S,F> cb=cbs.get(key);
			if(cb==null)
			{
				return;
			}
			removeCallBack(cb);
			switch(msg.what)
			{
				case SUCCESS:
				{
					S s=ss.get(key);
					ss.remove(key);
					cb.onSuccess(s);
				}
				break;
				case FAIL:
				{
					F f=fs.get(key);
					fs.remove(key);
					cb.onFail(f);
				}
				break;
			}
		}
	};

	private void addCallBack(ModelCallBack<S,F> cb)
	{
		if(cb==null)
		{
			return;
		}
		cbs.put(""+cb.hashCode(),cb);
	}

	protected void removeCallBack(ModelCallBack<S,F> cb)
	{
		if(cb==null)
		{
			return;
		}
		cbs.remove(""+cb.hashCode());
	}

	public void exec(final P p,final ModelCallBack<S,F> cb)
	{
		addCallBack(cb);
		es.execute(new Runnable()
		{
			@Override
			public void run()
			{
				exec(p,cb.hashCode());
			}
		});
	}

	protected abstract void exec(P p,int key);

	protected void postSuccess(int key,S s)
	{
		Message msg=Message.obtain();
		msg.what=SUCCESS;
		msg.arg1=key;
		ss.put(""+key,s);
		handler.sendMessage(msg);
	}

	protected void postFail(int key,F f)
	{
		Message msg=Message.obtain();
		msg.what=FAIL;
		msg.arg1=key;
		fs.put(key+"",f);
		handler.sendMessage(msg);
	}
}