package image;

import android.app.Fragment;
import android.app.FragmentManager;

import commons.base.BaseFragmentStatePagerAdapter;
import commons.base.BaseViewPagerFragment;

public class PreviewPagerAdapter extends BaseFragmentStatePagerAdapter<String>
{
	PreviewPagerAdapter(FragmentManager fm)
	{
		super(fm);
	}

	@Override
	public Fragment getItem(int position)
	{
		//		ImagePreviewFragment fragment=new ImagePreviewFragment();
		//		Bundle bundle=new Bundle();
		//		bundle.putString("path",data.get(position));
		//		bundle.putInt("position",position);
		//		fragment.setArguments(bundle);
		//		return fragment;
		BaseViewPagerFragment fragment=new BaseViewPagerFragment();
		PreviewFragmentAgent agent=new PreviewFragmentAgent();
		agent.setPath(data.get(position));
		fragment.setAgent(agent);
		return fragment;
	}
}