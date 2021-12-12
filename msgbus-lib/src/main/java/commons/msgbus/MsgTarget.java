package commons.msgbus;

/**
 * 消息接收者信息
 */
public class MsgTarget
{
	/**
	 * 方法ID
	 */
	int methodId;

	/**
	 * 消息ID
	 */
	int msgId;

	boolean sticky;

	/**
	 * 消息优先级
	 */
	int priority;

	/**
	 * 接收消息的线程
	 */
	int threadType;

	/**
	 * 接收消息的类
	 */
	Class<?> clazz;

	public MsgTarget(int msgId,int methodId,boolean sticky,int priority,int threadType,Class<?> clazz)
	{
		this.msgId=msgId;
		this.methodId=methodId;
		this.sticky=sticky;
		this.priority=priority;
		this.threadType=threadType;
		this.clazz=clazz;
	}
}