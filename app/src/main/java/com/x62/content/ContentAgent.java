package com.x62.content;

import commons.annotations.LayoutBind;
import commons.agent.BaseAgent;
import commons.utils.ViewBind;
import commons.widget.ContentLayout;
import com.x62.image.R;

@LayoutBind(R.layout.activity_content_layout)
public class ContentAgent extends BaseAgent
{
	@ViewBind.Bind(id=R.id.Content)
	private ContentLayout mContent;

	@Override
	public void initView()
	{
		mContent.addHeader(R.layout.content_test);
		mContent.setAdapter();
	}
}