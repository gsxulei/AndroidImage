package com.x62.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.x62.commons.annotations.LayoutBind;
import com.x62.commons.base.BaseActivity;
import com.x62.commons.utils.ViewBind;
import com.x62.commons.widget.ContentLayout;
import com.x62.image.R;

@LayoutBind(id=R.layout.activity_content_layout)
public class ContentActivity extends BaseActivity
{
	@ViewBind.Bind(id=R.id.Content)
	private ContentLayout mContent;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mContent.setLayoutManager(new LinearLayoutManager(this));
		mContent.addHeader(R.layout.content_test);
		mContent.setAdapter();
	}
}