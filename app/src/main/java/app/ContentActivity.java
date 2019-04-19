package app;

import commons.annotations.Agent;
import commons.agent.BaseActivity;
import com.x62.content.ContentAgent;

@Agent(ContentAgent.class)
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