package com.github.moduth.blockcanary;

/**
 * 修复CrashMonitor和BlockCanary之间的冲突
 */
public class BlockCanaryFix
{
	public static void fix()
	{
		BlockCanaryInternals.getInstance().monitor.println("<<<<< Finished to");
	}
}