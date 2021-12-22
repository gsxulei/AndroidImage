package com.cost.plugin;

import java.util.ArrayList;
import java.util.List;

public class CostConfig
{
	public List<String> list=new ArrayList<>();

	public void exclude(String className)
	{
		list.add(className);
	}

	public boolean isExclude(String className)
	{
		className=className.replaceAll("/",".");
		return list.contains(className);
	}
}