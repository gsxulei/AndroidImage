package commons.msgbus.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;

import commons.msgbus.MsgBus;
import commons.msgbus.MsgEvent;
import commons.msgbus.MsgReceiver;
import commons.msgbus.utils.MsgEventId;

public class MainActivity extends BaseActivity implements View.OnClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.button).setOnClickListener(this);
		findViewById(R.id.home).setOnClickListener(this);
	}

	@MsgReceiver(id=MsgEventId.ID_1001)
	void onSuccess(MsgEvent<List<String>> event)
	{
		Log.e("MsgBus","MainActivity.onSuccess->"+Thread.currentThread());
	}

	@MsgReceiver(id=MsgEventId.ID_1001, priority=100)
	static void onHello(MsgEvent<List<String>> event)
	{
		Log.e("MsgBus","MainActivity.onHello->"+event.t);
		//MsgBus.cancel(event);
	}

	@MsgReceiver(id=MsgEventId.ID_1002, priority=300)
	void onFail(MsgEvent<String> event)
	{
		Log.e("MsgBus","MainActivity.onFail");
	}

	@MsgReceiver(id=MsgEventId.ID_1004, sticky=true)
	void stickyData(MsgEvent<String> event)
	{
		Log.e("MsgBus","MainActivity.stickyData->"+this);
	}

	@Override
	public void onClick(View view)
	{
		if(view.getId()==R.id.button)
		{
			MsgBus.send(MsgEventId.ID_1003);
		}
		else if(view.getId()==R.id.home)
		{
			MsgBus.sendSticky(new MsgEvent<>(MsgEventId.ID_1004));
			startActivity(new Intent(this,HomeActivity.class));
		}
	}
}