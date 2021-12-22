package com.cost.plugin;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CostPlugin implements Plugin<Project>
{
	private static CostConfig config;

	@Override
	public void apply(Project project)
	{
		project.getExtensions().create("costConfig",CostConfig.class);
		AppExtension app=project.getExtensions().getByType(AppExtension.class);
		System.err.println("---cost---");
		app.registerTransform(new CostTransform());
		project.afterEvaluate(new AfterEvaluateAction());
	}

	public static boolean isExclude(String className)
	{
		if(config==null)
		{
			return false;
		}
		return config.isExclude(className);
	}

	public static class AfterEvaluateAction implements Action<Project>
	{
		@Override
		public void execute(Project project)
		{
			config=project.getExtensions().getByType(CostConfig.class);
			System.err.println("---config---"+config);
			System.err.println("---config---"+config.list);
		}
	}
}