package commons.app.base;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Method;

/**
 * 主要用于提供无入侵的全局的Application、Context
 *
 * @author 徐雷
 */
public class AppBase
{
	private Context mCtx;
	private Application app;

	/**
	 * 单例加载器
	 */
	private static class Loader
	{
		private static final AppBase INSTANCE=new AppBase();
	}

	public static AppBase getInstance()
	{
		return Loader.INSTANCE;
	}

	private AppBase()
	{
		initApplication();
	}

	/**
	 * 初始化Application
	 */
	private void initApplication()
	{
		try
		{
			/**
			 * 可以直接使用ActivityThread.currentApplication()
			 * 需要导包${sdk.dir}/platforms/android-xx/data/layoutlib.jar
			 */
			Class<?> clazz=Class.forName("android.app.ActivityThread");
			Method currentApplication=clazz.getMethod("currentApplication");
			app=(Application)currentApplication.invoke(null,(Object[])null);
			mCtx=app;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		// if(app!=null)
		// {
		// return;
		// }

		/**
		 * 可以直接使用AppGlobals.getInitialApplication()
		 * 需要导包${sdk.dir}/platforms/android-xx/data/layoutlib.jar
		 */
		// try
		// {
		// app=(Application)Class.forName("android.app.AppGlobals").getMethod("getInitialApplication")
		// .invoke(null,(Object[])null);
		// }
		// catch(Exception e)
		// {
		// e.printStackTrace();
		// }
	}

	public void setContext(Application app)
	{
		this.app=app;
	}

	/**
	 * 获取Context
	 *
	 * @return Context
	 */
	public Context getContext()
	{
		if(mCtx!=null)
		{
			return mCtx;
		}
		return app;
	}

	public Application getApplication()
	{
		return app;
	}
}