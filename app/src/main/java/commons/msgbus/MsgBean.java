package commons.msgbus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MsgBean
{
	/**
	 * 消息ID
	 */
	int id;

	/**
	 * 注册该消息的对象方法
	 */
	List<MsgTarget> targets=new CopyOnWriteArrayList<>();

	public List<MsgTarget> getTargets()
	{
		List<MsgTarget> result=new ArrayList<>(targets);
		Collections.sort(result,new Comparator<MsgTarget>()
		{
			@Override
			public int compare(MsgTarget lhs,MsgTarget rhs)
			{
				//降序排列
				int result=0;
				if(lhs.priority>rhs.priority)
				{
					result=-1;
				}
				else if(lhs.priority<rhs.priority)
				{
					result=1;
				}
				return result;
			}
		});
		return result;
	}
}