package com.x62.activity;

import commons.annotations.Agent;
import commons.annotations.LayoutBind;
import commons.base.BaseActivity;
import com.x62.content.ContentAgent;
import com.x62.image.R;

@Agent(ContentAgent.class)
@LayoutBind(R.layout.activity_content_layout)
public class ContentActivity extends BaseActivity
{
	//	@ViewBind.Bind(id=R.id.Content)
	//	private ContentLayout mContent;
	//
	//	@Override
	//	protected void onCreate(@Nullable Bundle savedInstanceState)
	//	{
	//		super.onCreate(savedInstanceState);
	//
	//		//mContent.setLayoutManager(new LinearLayoutManager(this));
	//		mContent.addHeader(R.layout.content_test);
	//		mContent.setAdapter();
	//	}
}