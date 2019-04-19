package image;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.x62.image.R;
import commons.utils.MsgEventId;

import java.util.List;

import commons.annotations.LayoutBind;
import commons.agent.BaseAgent;
import commons.msgbus.MsgBus;
import commons.msgbus.MsgEvent;
import commons.msgbus.MsgReceiver;
import commons.utils.SysBarUtils;
import commons.utils.ViewBind;

@LayoutBind(R.layout.commons_activity_image_preview)
public class PreviewAgent extends BaseAgent
{
	@ViewBind.Bind(id=R.id.pager)
	private ViewPager mPager;

	@Override
	public void initView()
	{
		super.initView();
		SysBarUtils.sysBarFloat((Activity)mContext);
	}

	/**
	 * 初始化数据
	 *
	 * @param event 图片路径
	 */
	@MsgReceiver(id=MsgEventId.ID_100002, sticky=true)
	private void initImageData(MsgEvent<List<String>> event)
	{
		MsgBus.cancelSticky(event);
		Bundle bundle=getParamBundle();
		int position=bundle.getInt("position",0);
		Activity activity=(Activity)mContext;
		PreviewPagerAdapter adapter=new PreviewPagerAdapter(activity.getFragmentManager());
		adapter.setData(event.t);
		mPager.setAdapter(adapter);
		mPager.setCurrentItem(position);
	}
}