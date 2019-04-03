package commons.base;

/**
 * 用于和ViewPager一起使用
 */
public class BaseViewPagerFragment extends BaseFragment
{
	//	protected boolean isVisible=false;// 当前Fragment是否可见
	//
	//	@Override
	//	public void setUserVisibleHint(boolean isVisibleToUser)
	//	{
	//		super.setUserVisibleHint(isVisibleToUser);
	//		isVisible=isVisibleToUser;
	//		if(isVisibleToUser)
	//		{
	//			lazyLoadData();
	//		}
	//	}
	//
	//	public void lazyLoadData()
	//	{
	//		if(!isFirstLoad||!isVisible||!isInitView)
	//		{
	//			return;
	//		}
	//		mAgent.initData();
	//		isFirstLoad=false;
	//	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);
		isVisible=isVisibleToUser;
		if(isVisibleToUser)
		{
			lazyLoadData();
		}
	}

	public void lazyLoadData()
	{
		if(!isFirstLoad||!isInitView)
		{
			return;
		}
		mAgent.initData();
		isFirstLoad=false;
	}
}
