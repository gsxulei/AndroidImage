package commons.msgbus;

import android.app.Activity;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MsgBus
{
	// 线程池
	private static final ExecutorService POOL=Executors.newCachedThreadPool();

	private static final Lock LOCK=new ReentrantLock();
	private static final SparseArray<List<MsgTarget>> TARGETS=new SparseArray<>();
	private static final Map<Class<?>,List<Object>> OBJECTS=new HashMap<>();
	private static final Map<Class<?>,MsgBusCaller<?>> CALLERS=new HashMap<>();

	//事件状态
	private static final Map<Object,LinkedBlockingQueue<Boolean>> EVENT_STATE=new ConcurrentHashMap<>(1);

	private static final List<MsgEvent<?>> STICKY_EVENT=new CopyOnWriteArrayList<>();
	private static final Handler MAIN_HANDLER=new Handler(Looper.getMainLooper());

	//降序排列
	private static final Comparator<MsgTarget> COMPARATOR=(lhs,rhs)->rhs.priority-lhs.priority;

	static
	{
		try
		{
			LOCK.lock();
			MsgBusCallerController.init();
			int size=TARGETS.size();
			for(int i=0;i<size;i++)
			{
				List<MsgTarget> targets=TARGETS.get(TARGETS.keyAt(i));
				if(targets!=null&&targets.size()>0)
				{
					Collections.sort(targets,COMPARATOR);
				}
			}
		}
		finally
		{
			LOCK.unlock();
		}
	}

	/**
	 * 注册消息接收
	 *
	 * @param obj 含有消息接收方法的对象
	 */
	public static void register(Object obj)
	{
		addOrRemoveObject(obj,true);
	}

	/**
	 * 解绑
	 *
	 * @param obj 需解绑的对象
	 */
	public static void unregister(Object obj)
	{
		addOrRemoveObject(obj,false);
	}

	private static void addOrRemoveObject(Object object,boolean isAdd)
	{
		if(object==null)
		{
			return;
		}

		try
		{
			LOCK.lock();
			for(Class<?> clazz : OBJECTS.keySet())
			{
				if(clazz.isAssignableFrom(object.getClass())||object==clazz)
				{
					List<Object> list=OBJECTS.get(clazz);
					if(list==null||(list.contains(object)&&isAdd))
					{
						continue;
					}
					if(isAdd)
					{
						list.add(object);
						checkSticky(clazz);
					}
					else
					{
						list.remove(object);
					}
				}
			}
		}
		finally
		{
			LOCK.unlock();
		}
	}

	public static void send(int id)
	{
		send(new MsgEvent<>(id));
	}

	public static void send(final MsgEvent<?> event)
	{
		POOL.submit(()->asyncSend(TARGETS.get(event.id),event));
	}

	/**
	 * 结束消息
	 *
	 * @param event 消息对象
	 */
	public static void cancel(MsgEvent<?> event)
	{
		setEventState(EVENT_STATE.get(event),false);
	}

	public static void cancelSticky(MsgEvent<?> event)
	{
		synchronized(STICKY_EVENT)
		{
			STICKY_EVENT.remove(event);
			cancel(event);
		}
	}

	/**
	 * 发送黏性消息
	 *
	 * @param event 消息对象
	 */
	public static void sendSticky(MsgEvent<?> event)
	{
		synchronized(STICKY_EVENT)
		{
			STICKY_EVENT.add(event);
			checkSticky(null);
		}
	}

	private static void asyncSend(List<MsgTarget> list,MsgEvent<?> event)
	{
		if(list==null||list.size()==0)
		{
			return;
		}

		LinkedBlockingQueue<Boolean> queue=EVENT_STATE.get(event);
		if(queue==null)
		{
			queue=new LinkedBlockingQueue<>();
			setEventState(queue,true);
			EVENT_STATE.put(event,queue);
		}

		for(MsgTarget target : list)
		{
			MsgBusCaller<?> caller=CALLERS.get(target.clazz);
			boolean state=getEventState(queue);

			if(state)
			{
				Set<Object> objects=getObjectByClass(target.clazz);
				for(Object object : objects)
				{
					dispatch(target,caller,object,event);
				}
			}
			else
			{
				break;
			}
		}
		EVENT_STATE.remove(event);
	}

	private static void checkSticky(Class<?> clazz)
	{
		POOL.submit(()->
		{
			for(MsgEvent<?> event : STICKY_EVENT)
			{
				asyncSend(getStickyTarget(event,clazz),event);
			}
		});
	}

	private static List<MsgTarget> getStickyTarget(MsgEvent<?> event,Class<?> clazz)
	{
		List<MsgTarget> list=TARGETS.get(event.id);
		List<MsgTarget> stickyTarget=new ArrayList<>();
		for(MsgTarget target : list)
		{
			if(target.sticky&&(clazz==null||clazz==target.clazz))
			{
				stickyTarget.add(target);
			}
		}
		return stickyTarget;
	}

	private static void dispatch(MsgTarget target,MsgBusCaller<?> caller,Object object,MsgEvent<?> event)
	{
		if(target.threadType==MsgReceiver.THREAD_MAIN)
		{
			MAIN_HANDLER.post(()->exec(caller,object,event,target.methodId));
		}
		else if(target.threadType==MsgReceiver.THREAD_BACKGROUND)
		{
			POOL.submit(()->exec(caller,object,event,target.methodId));
		}
	}

	@SuppressWarnings("unchecked")
	private static void exec(MsgBusCaller caller,Object object,MsgEvent<?> event,int index)
	{
		try
		{
			if(object instanceof Class)
			{
				object=null;
			}
			caller.call(object,event,index);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			setEventState(EVENT_STATE.get(event),true);
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

	private static Set<Object> getObjectByClass(Class<?> targetClazz)
	{
		Set<Object> objects=new HashSet<>();
		try
		{
			LOCK.lock();
			for(Class<?> clazz : OBJECTS.keySet())
			{
				if(clazz.isAssignableFrom(targetClazz))
				{
					List<Object> list=OBJECTS.get(clazz);
					if(list!=null)
					{
						for(Object object : list)
						{
							if(targetClazz.isAssignableFrom(object.getClass())||object==targetClazz)
							{
								objects.add(object);
							}
						}
					}
				}
			}
		}
		finally
		{
			LOCK.unlock();
		}
		return objects;
	}

	static void addTarget(MsgTarget target)
	{
		if(target==null)
		{
			return;
		}
		List<MsgTarget> list=TARGETS.get(target.msgId);
		if(list==null)
		{
			list=new ArrayList<>();
			TARGETS.put(target.msgId,list);
		}
		if(target.threadType==MsgReceiver.THREAD_AUTO)
		{
			boolean flag=Activity.class.isAssignableFrom(target.clazz);
			flag=flag||Fragment.class.isAssignableFrom(target.clazz);
			flag=flag||View.class.isAssignableFrom(target.clazz);
			target.threadType=flag?MsgReceiver.THREAD_MAIN:MsgReceiver.THREAD_BACKGROUND;
		}
		list.add(target);
	}

	static void addCaller(Class<?> clazz,MsgBusCaller<?> caller)
	{
		CALLERS.put(clazz,caller);
	}

	static void initObject(Class<?> clazz)
	{
		OBJECTS.put(clazz,new ArrayList<>());
	}
}