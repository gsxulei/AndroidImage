package commons.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import commons.annotations.Agent;
import commons.annotations.LayoutBind;
import commons.msgbus.MsgBus;
import commons.utils.ViewBind;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener
{
	public Context mContext;
	private Toast mToast;

	private BaseAgent agent;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext=this;

		int layoutId=getLayoutId();
		if(layoutId>0)
		{
			setContentView(layoutId);
		}

		ViewBind.bind(this);

		//注册消息
		MsgBus.register(this);

		initAgent();
	}

	private int getLayoutId()
	{
		int layoutId=0;
		LayoutBind layoutBind=getClass().getAnnotation(LayoutBind.class);
		if(layoutBind!=null)
		{
			layoutId=layoutBind.value();
		}
		return layoutId;
	}

	private void initAgent()
	{
		Agent bind=getClass().getAnnotation(Agent.class);
		if(bind==null)
		{
			return;
		}
		Class<? extends BaseAgent> clazz=bind.value();
		try
		{
			agent=clazz.newInstance();
			agent.mContext=this;

			ViewBind.bind(agent,getWindow().getDecorView());
			agent.initView();
			MsgBus.register(agent);
			agent.initData();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v)
	{
		if(agent!=null)
		{
			agent.onClick(v);
		}
	}

	/**
	 * 显示Toast
	 *
	 * @param text 显示内容
	 */
	public void toast(String text)
	{
		if(mToast==null)
		{
			mToast=Toast.makeText(this,"",Toast.LENGTH_SHORT);
		}
		mToast.setText(text);
		mToast.show();
	}

	public void toast(int resId)
	{
		toast(getResources().getString(resId));
		//		if(mToast==null)
		//		{
		//			mToast=Toast.makeText(this,"",Toast.LENGTH_SHORT);
		//		}
		//		mToast.setText(resId);
		//		mToast.show();
	}

	/**
	 * 打开页面
	 *
	 * @param clazz 页面Activity
	 */
	public void open(Class<?> clazz)
	{
		open(clazz,null);
	}

	/**
	 * 打开页面
	 *
	 * @param clazz  页面Activity
	 * @param bundle 参数
	 */
	public void open(Class<?> clazz,Bundle bundle)
	{
		Intent intent=new Intent(mContext,clazz);
		if(bundle!=null)
		{
			intent.putExtra("PageParams",bundle);
		}
		startActivity(intent);
	}

	public Bundle getParamBundle()
	{
		Intent intent=getIntent();
		Bundle bundle=null;
		if(intent!=null)
		{
			bundle=intent.getBundleExtra("PageParams");
		}
		if(bundle==null)
		{
			bundle=new Bundle();
		}
		return bundle;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		//解绑消息
		MsgBus.unregister(this);

		if(agent!=null)
		{
			MsgBus.unregister(agent);
		}
	}
}