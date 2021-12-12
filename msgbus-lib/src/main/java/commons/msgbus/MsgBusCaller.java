package commons.msgbus;

public interface MsgBusCaller<T>
{
	void call(T object,MsgEvent<?> event,int index);
}