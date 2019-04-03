package commons.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.x62.image.R;

import java.util.Stack;

import commons.annotations.Agent;
import commons.msgbus.MsgBus;
import commons.msgbus.MsgEvent;
import commons.msgbus.MsgReceiver;
import commons.utils.MsgEventId;
import commons.utils.ResUtils;
import commons.utils.ViewBind;
import commons.widget.LoadingDialog;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener
{
	public Context mContext;
	private Toast mToast;
	private LoadingDialog loadingDialog;

	private BaseAgent mAgent;

	protected static final Stack<Activity> activityStack=new Stack<>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext=this;
		activityStack.push(this);

		initAgent();

		if(mAgent!=null)
		{
			int layoutId=mAgent.getLayoutId();
			if(layoutId>0)
			{
				setContentView(layoutId);
			}
			ViewBind.bind(mAgent,getWindow().getDecorView());
			mAgent.initView();
			MsgBus.register(mAgent);
			mAgent.initData();
		}

		ViewBind.bind(this);

		//注册消息
		MsgBus.register(this);
	}

	//	private int getLayoutId()
	//	{
	//		int layoutId=0;
	//		LayoutBind layoutBind=getClass().getAnnotation(LayoutBind.class);
	//		if(layoutBind!=null)
	//		{
	//			layoutId=layoutBind.value();
	//		}
	//		return layoutId;
	//	}

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
			//mAgent=clazz.newInstance();
			mAgent=BaseAgent.getAgent(clazz);
			mAgent.mContext=this;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v)
	{
		if(mAgent!=null)
		{
			mAgent.onClick(v);
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

	public void showLoading(String text)
	{
		if(loadingDialog==null)
		{
			loadingDialog=new LoadingDialog(this);
			loadingDialog.setLoadingText(text);
		}
		loadingDialog.show();
	}

	public void showLoading()
	{
		showLoading(ResUtils.getString(R.string.commons_please_wait));
	}

	public void hideLoading()
	{
		if(loadingDialog!=null)
		{
			loadingDialog.dismiss();
		}
	}

	@MsgReceiver(id=MsgEventId.ID_100001)
	void toast(MsgEvent<String> event)
	{
		if(this!=activityStack.lastElement())
		{
			return;
		}
		MsgBus.cancel(event);
		hideLoading();
		toast(event.t);
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

		if(mAgent!=null)
		{
			MsgBus.unregister(mAgent);
			mAgent.onDestroy();
		}
		pop();
	}

	public void pop()
	{
		if(this==activityStack.lastElement())
		{
			activityStack.pop();
		}
	}
}