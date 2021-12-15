package commons.msgbus.model;

import android.util.Log;

import commons.msgbus.MsgBus;
import commons.msgbus.MsgEvent;
import commons.msgbus.MsgReceiver;
import commons.msgbus.utils.MsgEventId;

public class Model
{
	@MsgReceiver(id=MsgEventId.ID_1003)
	static void getData(MsgEvent<String> event)
	{
		Log.e("MsgBus","Model.getData->"+Thread.currentThread());
		MsgBus.send(MsgEventId.ID_1001);
	}
}