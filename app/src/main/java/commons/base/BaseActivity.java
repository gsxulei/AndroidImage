package commons.base;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import commons.msgbus.MsgBus;

public abstract class BaseActivity extends Activity
{
	private static final String PAGE_PARAMS="PageParams";
	public static Class<?> last;

	public Activity mContext;

	private Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mContext=this;

		MsgBus.register(this);

		Intent intent=getIntent();

		Class<?> clazz=(Class<?>)intent.getSerializableExtra("clazz");
		if(clazz==null)
		{
			clazz=getDefaultFragment();
		}
		Bundle bundle=intent.getBundleExtra(PAGE_PARAMS);

		FragmentManager fm=getFragmentManager();
		FragmentTransaction transaction=fm.beginTransaction();

		if(savedInstanceState==null)
		{
			fragment=getFragment(clazz);
			fragment.setArguments(bundle);
			transaction.add(Window.ID_ANDROID_CONTENT,fragment,clazz.getName());
		}
		else
		{
			fragment=fm.findFragmentByTag(clazz.getName());
		}
		transaction.show(fragment);
		transaction.commit();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		MsgBus.unregister(this);
	}

	@Override
	public void onBackPressed()
	{
		if(((BaseFragment)fragment).onBackPressed())
		{
			return;
		}
		super.onBackPressed();
	}

	public Fragment getFragment(Class<?> clazz)
	{
		Fragment fragment=null;
		try
		{
			fragment=(Fragment)clazz.newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fragment;
	}

	/**
	 * 打开新界面
	 *
	 * @param clazz 新界面Fragment
	 */
	public void open(Class<? extends Fragment> clazz)
	{
		open(clazz,new Bundle());
	}

	/**
	 * 打开新界面
	 *
	 * @param clazz  新界面Fragment
	 * @param bundle 参数
	 */
	public void open(Class<?> clazz,Bundle bundle)
	{
		//避免重复打开界面
		if(last==clazz)
		{
			return;
		}
		last=clazz;

		Intent intent=new Intent(mContext,getClass());
		intent.putExtra("clazz",clazz);
		if(bundle!=null)
		{
			intent.putExtra(PAGE_PARAMS,bundle);
		}
		startActivity(intent);
	}

	/**
	 * 获取Activity当前的Fragment
	 *
	 * @return Fragment
	 */
	public Fragment getCurrentFragment()
	{
		return fragment;
	}

	/**
	 * 获取默认Fragment(第一个界面Fragment)
	 *
	 * @return 默认Fragment
	 */
	public abstract Class<? extends Fragment> getDefaultFragment();
}