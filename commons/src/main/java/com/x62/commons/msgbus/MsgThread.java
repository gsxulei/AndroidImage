package com.x62.commons.msgbus;

/**
 * 线程类型
 */
public enum MsgThread
{
	/**
	 * 主线程
	 */
	MAIN,

	/**
	 * 相同线程
	 */
	SAME,

	/**
	 * 子线程
	 */
	BACKGROUND
}