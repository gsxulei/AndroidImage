package com.x62.content;

import commons.base.BaseAgent;
import commons.utils.ViewBind;
import commons.widget.ContentLayout;
import com.x62.image.R;

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