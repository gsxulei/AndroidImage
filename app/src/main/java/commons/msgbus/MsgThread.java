package commons.msgbus;

/**
 * 线程类型
 */
interface MsgThread
{
	/**
	 * 主线程
	 */
	int MAIN=0;

	/**
	 * 相同线程
	 */
	int SAME=1;

	/**
	 * 子线程
	 */
	int BACKGROUND=2;
}