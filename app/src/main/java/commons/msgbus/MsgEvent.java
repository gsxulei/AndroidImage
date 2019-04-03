package commons.msgbus;

public class MsgEvent<T>
{
	public MsgEvent()
	{
	}

	public MsgEvent(int id)
	{
		this.id=id;
	}

	public MsgEvent(int id,T t)
	{
		this.id=id;
		this.t=t;
	}

	/**
	 * 消息ID
	 */
	public int id;

	/**
	 * 消息携带的数据
	 */
	public T t;
}