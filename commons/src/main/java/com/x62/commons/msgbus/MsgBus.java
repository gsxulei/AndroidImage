package com.x62.commons.msgbus;

import android.app.Activity;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消息总线,简化版EventBus
 */
public class MsgBus
{
	/**
	 * 线程池
	 */
	private static final ExecutorService POOL=Executors.newCachedThreadPool();

	private static List<MsgBean> msgList=new CopyOnWriteArrayList<>();
	private static Map<Class<?>,List<Method>> METHOD_CACHE=new ConcurrentHashMap<>();
	private static Map<Object,List<MsgBean>> map=new ConcurrentHashMap<>();

	/**
	 * 事件状态
	 */
	private static Map<Object,LinkedBlockingQueue<Boolean>> eventState=new ConcurrentHashMap<>(1);

	private static final List<MsgEvent<?>> stickyEvent=new CopyOnWriteArrayList<>();

	private static Handler handler=new Handler(Looper.getMainLooper())
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);

			switch(msg.what)
			{
				case 1:
				{
					Object[] objArr=(Object[])msg.obj;
					MsgTarget target=null;
					MsgEvent event=null;
					if(objArr[0] instanceof MsgTarget)
					{
						target=(MsgTarget)objArr[0];
					}
					if(objArr[1] instanceof MsgEvent)
					{
						event=(MsgEvent)objArr[1];
					}
					if(target!=null&&event!=null)
					{
						exec(target,event);
					}
				}
				break;
			}
		}
	};

	/**
	 * 注册消息接收
	 *
	 * @param obj 含有消息接收方法的对象
	 */
	public static void register(Object obj)
	{
		if(obj==null)
		{
			return;
		}
		register(obj.getClass(),obj);
	}

	/**
	 * 注册消息接收
	 *
	 * @param clazz 含有消息接收方法的类,用于注册静态方法
	 */
	public static void register(Class<?> clazz)
	{
		if(clazz==null)
		{
			return;
		}
		register(clazz,null);
	}

	private static void register(Class<?> clazz,Object obj)
	{
		//已注册过则不重复注册
		List<MsgBean> list=(obj==null)?map.get(clazz):map.get(obj);
		if(list!=null)
		{
			return;
		}

		//从方法缓存中取出当前类的方法
		List<Method> methods=METHOD_CACHE.get(clazz);
		if(methods==null)
		{
			methods=getMethods(clazz);
			if(methods==null||methods.size()<=0)
			{
				return;
			}
			METHOD_CACHE.put(clazz,methods);
		}

		List<MsgBean> temp=new ArrayList<>();
		for(Method method : methods)
		{
			MsgReceiver receiver=method.getAnnotation(MsgReceiver.class);
			MsgBean msg=getMsgById(receiver.id());
			if(msg==null)
			{
				msg=new MsgBean();
				msg.id=receiver.id();
			}

			MsgTarget target=new MsgTarget();

			target.method=method;
			target.object=obj;
			target.id=receiver.id();
			target.priority=receiver.priority();
			target.sticky=receiver.sticky();
			target.threadType=receiver.threadType();

			boolean flag=obj instanceof Activity;
			flag=flag||obj instanceof Fragment;
			flag=flag||obj instanceof View;
			flag=flag||obj instanceof android.support.v4.app.Fragment;
			if(flag)
			{
				target.threadType=MsgThread.MAIN;
			}

			msg.targets.add(target);
			//Log.e("xulei","注册->"+target.object+","+target.method);

			if(target.sticky)
			{
				stickyInit(target);
			}

			temp.add(msg);
			msgList.add(msg);
		}
		if(obj==null)
		{
			obj=clazz;
		}
		map.put(obj,temp);
	}

	/**
	 * 解绑
	 *
	 * @param obj 需解绑的对象
	 */
	public static void unregister(Object obj)
	{
		List<MsgBean> list=map.get(obj);
		if(list==null||list.size()<=0)
		{
			return;
		}
		for(MsgBean msg : list)
		{
			MsgTarget target=new MsgTarget();
			target.object=obj;
			msg.targets.remove(target);
		}
	}

	public static void unregisterClass(Class clazz)
	{
		if(clazz!=null)
		{
			unregister(clazz);
		}
	}

	public static void send(final MsgEvent event)
	{
		POOL.submit(new Runnable()
		{
			@Override
			public void run()
			{
				asyncSend(event);
			}
		});
	}

	public static void send(int id)
	{
		MsgEvent<String> event=new MsgEvent<>(id);
		send(event);
	}

	private static void asyncSend(MsgEvent event)
	{
		MsgBean msg=getMsgById(event.id);
		if(msg==null||msg.targets.size()<=0)
		{
			return;
		}

		LinkedBlockingQueue<Boolean> queue=eventState.get(event);
		if(queue==null)
		{
			queue=new LinkedBlockingQueue<>();
			setEventState(queue,true);
			eventState.put(event,queue);
		}
		//Log.e("xulei","msg.targets.size()->"+msg.targets.size());
		List<MsgTarget> targets=msg.getTargets();
		for(MsgTarget target : targets)
		{
			boolean state=getEventState(queue);
			//Log.e("xulei","target"+target+",state->"+state);
			if(state)
			{
				dispatch(target,event);
			}
			else
			{
				//Log.e("xulei","事件取消");
				break;
			}
		}
		eventState.remove(event);
	}

	private static void stickyInit(final MsgTarget target)
	{
		POOL.submit(new Runnable()
		{
			@Override
			public void run()
			{
				for(MsgEvent event : MsgBus.stickyEvent)
				{
					if(event.id==target.id)
					{
						dispatch(target,event);
					}
				}
			}
		});
	}

	private static void dispatch(final MsgTarget target,final MsgEvent event)
	{
		switch(target.threadType)
		{
			case MAIN:
			{
				if(isMainThread())
				{
					exec(target,event);
					return;
				}
				Message message=new Message();
				message.what=1;
				message.obj=new Object[]{target,event};
				handler.sendMessage(message);
			}
			break;

			case SAME:
			{
				exec(target,event);
			}
			break;

			case BACKGROUND:
			{
				POOL.submit(new Runnable()
				{
					@Override
					public void run()
					{
						exec(target,event);
					}
				});
			}
			break;
		}
	}

	private static void exec(MsgTarget target,MsgEvent event)
	{
		try
		{
			target.method.setAccessible(true);
			target.method.invoke(target.object,event);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			setEventState(eventState.get(event),true);
		}
	}

	/**
	 * 设置消息状态
	 *
	 * @param queue 状态存储器
	 * @param state 状态值
	 */
	private static void setEventState(LinkedBlockingQueue<Boolean> queue,boolean state)
	{
		if(queue==null)
		{
			return;
		}
		try
		{
			queue.put(state);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static boolean getEventState(LinkedBlockingQueue<Boolean> queue)
	{
		boolean state=false;
		if(queue==null)
		{
			return false;
		}

		try
		{
			state=queue.take();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return state;
	}

	/**
	 * 结束消息
	 *
	 * @param event 消息对象
	 */
	public static void cancel(MsgEvent event)
	{
		setEventState(eventState.get(event),false);
	}

	public static void cancelSticky(MsgEvent event)
	{
		synchronized(stickyEvent)
		{
			stickyEvent.remove(event);
		}
		cancel(event);
	}

	/**
	 * 发送
	 *
	 * @param event 消息对象
	 */
	public static void sendSticky(MsgEvent event)
	{
		synchronized(stickyEvent)
		{
			stickyEvent.add(event);
		}
		send(event);
	}

	private static List<Method> getMethods(Class<?> clazz)
	{
		List<Method> result=new CopyOnWriteArrayList<>();
		while(clazz!=null)
		{
			//过滤掉系统类
			String name=clazz.getName();
			boolean flag=name.startsWith("java.");
			flag=flag||name.startsWith("javax.");
			flag=flag||name.startsWith("android.");

			if(flag)
			{
				break;
			}

			Method[] methods=clazz.getDeclaredMethods();
			for(Method method : methods)
			{
				MsgReceiver receiver=method.getAnnotation(MsgReceiver.class);
				//if(method.isAnnotationPresent(MsgReceiver.class))
				if(receiver!=null)
				{
					result.add(method);
				}
			}

			clazz=clazz.getSuperclass();
		}

		return result;
	}

	private static MsgBean getMsgById(int id)
	{
		MsgBean result=null;
		for(MsgBean msg : msgList)
		{
			if(id==msg.id)
			{
				result=msg;
				break;
			}
		}
		return result;
	}

	/**
	 * 判断当前线程是否为主线程
	 *
	 * @return true-主线程,false-非主线程
	 */
	private static boolean isMainThread()
	{
		return Looper.myLooper()==Looper.getMainLooper();
	}
}