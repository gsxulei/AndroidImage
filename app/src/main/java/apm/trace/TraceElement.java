package apm.trace;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TraceElement
{
	public long startTime;
	public long endTime;
	public StackTraceElement self;
	List<TraceElement> children=new ArrayList<>();

	@Override
	public String toString()
	{
		JSONObject json=new JSONObject();

		try
		{
			json.put("time",endTime-startTime);
			json.put("self",self.getClassName()+"."+self.getMethodName());
			if(children.size()>0)
			{
				JSONArray array=new JSONArray();
				json.put("children",array);

				for(TraceElement element : children)
				{
					array.put(element.toString());
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return json.toString();
	}
}