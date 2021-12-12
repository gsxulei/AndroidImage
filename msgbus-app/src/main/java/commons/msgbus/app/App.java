package commons.msgbus.app;

import android.app.Application;

import commons.msgbus.MsgBus;
import commons.msgbus.model.Model;

public class App extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		MsgBus.register(Model.class);
	}
}