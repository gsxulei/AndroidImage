package com.x62.commons.msgbus;

import java.lang.reflect.Method;

/**
 * 消息接收者信息
 */
public class MsgTarget
{
	/**
	 * 接收消息的方法
	 */
	Method method;

	/**
	 * 接收消息的对象
	 */
	Object object;

	/**
	 * 消息ID
	 */
	int id;

	boolean sticky;

	/**
	 * 消息优先级
	 */
	int priority;

	/**
	 * 接收消息的线程
	 */
	MsgThread threadType;

	@Override
	public boolean equals(Object o)
	{
		if(o==null||!(o instanceof MsgTarget))
		{
			return false;
		}
		return object==((MsgTarget)o).object;
	}
}