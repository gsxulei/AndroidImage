package commons.msgbus.app;

import android.os.Bundle;
import android.util.Log;

import commons.msgbus.MsgBus;
import commons.msgbus.MsgEvent;
import commons.msgbus.MsgReceiver;
import commons.msgbus.utils.MsgEventId;

public class HomeActivity extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@MsgReceiver(id=MsgEventId.ID_1004, sticky=true)
	void initData(MsgEvent<String> event)
	{
		Log.e("MsgBus","HomeActivity.initData->"+this);
		MsgBus.cancelSticky(event);
	}
}