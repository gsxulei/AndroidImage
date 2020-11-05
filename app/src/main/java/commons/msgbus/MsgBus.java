package commons.msgbus;

import android.app.Activity;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
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
	private static final Thread.UncaughtExceptionHandler defCatcher;
	private static final boolean isSysCatcher;

	/**
	 * 事件状态
	 */
	private static Map<Object,LinkedBlockingQueue<Boolean>> eventState=new ConcurrentHashMap<>(1);

	private static final List<MsgEvent<?>> stickyEvent=new CopyOnWriteArrayList<>();

	private static final Handler MAIN_HANDLER=new Handler(Looper.getMainLooper());

	static
	{
		defCatcher=Thread.getDefaultUncaughtExceptionHandler();

		//com.android.internal.os.RuntimeInit$UncaughtHandler
		//com.android.internal.os.RuntimeInit$KillApplicationHandler 8.0+
		isSysCatcher=(defCatcher==null||defCatcher.getClass().getName().startsWith("com.android.internal.os"));
	}

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
			if(methods.size()<=0)
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
			if(flag)
			{
				target.threadType=MsgThread.MAIN;
			}

			msg.targets.add(target);

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
		map.remove(obj);
	}

	public static void unregisterClass(Class<?> clazz)
	{
		if(clazz!=null)
		{
			unregister(clazz);
		}
	}

	public static void send(final MsgEvent<?> event)
	{
		POOL.submit(()->asyncSend(event));
	}

	public static void send(int id)
	{
		MsgEvent<String> event=new MsgEvent<>(id);
		send(event);
	}

	private static void asyncSend(MsgEvent<?> event)
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

		List<MsgTarget> targets=msg.getTargets();
		for(MsgTarget target : targets)
		{
			boolean state=getEventState(queue);

			if(state)
			{
				dispatch(target,event);
			}
			else
			{
				break;
			}
		}
		eventState.remove(event);
	}

	private static void stickyInit(final MsgTarget target)
	{
		POOL.submit(()->
		{
			for(MsgEvent<?> event : MsgBus.stickyEvent)
			{
				if(event.id==target.id)
				{
					dispatch(target,event);
				}
			}
		});
	}

	private static void dispatch(final MsgTarget target,final MsgEvent<?> event)
	{
		switch(target.threadType)
		{
			case MsgThread.MAIN:
			{
				if(isMainThread())
				{
					exec(target,event);
					return;
				}
				MAIN_HANDLER.post(()->exec(target,event));
			}
			break;

			case MsgThread.SAME:
			{
				exec(target,event);
			}
			break;

			case MsgThread.BACKGROUND:
			{
				POOL.submit(()->exec(target,event));
			}
			break;
		}
	}

	private static void exec(MsgTarget target,MsgEvent<?> event)
	{
		try
		{
			target.method.setAccessible(true);
			target.method.invoke(target.object,event);
		}
		catch(Exception e)
		{
			catchException(e);
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
			catchException(e);
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
			catchException(e);
		}
		return state;
	}

	/**
	 * 结束消息
	 *
	 * @param event 消息对象
	 */
	public static void cancel(MsgEvent<?> event)
	{
		setEventState(eventState.get(event),false);
	}

	public static void cancelSticky(MsgEvent<?> event)
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
	public static void sendSticky(MsgEvent<?> event)
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

	private static void catchException(Exception e)
	{
		if(isSysCatcher)
		{
			e.printStackTrace();
		}
		else
		{
			defCatcher.uncaughtException(Thread.currentThread(),e);
		}
	}
}