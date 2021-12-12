package commons.msgbus.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import commons.msgbus.MsgBus;
import commons.msgbus.MsgEvent;
import commons.msgbus.MsgReceiver;
import commons.msgbus.utils.MsgEventId;

public class BaseActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		MsgBus.register(this);
	}

	@MsgReceiver(id=MsgEventId.ID_1001, priority=-1)
	void onWorld(MsgEvent<String> event)
	{
		Log.e("MsgBus","BaseActivity.onWorld->"+this);
		//MsgBus.cancel(event);
		MsgBus.send(MsgEventId.ID_1002);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		MsgBus.unregister(this);
	}
}