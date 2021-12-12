package commons.msgbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消息接收者<br/>
 * <a href="https://www.jianshu.com/p/33d92e827f80">解决Method '...' is never used警告</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface MsgReceiver
{
	/**
	 * 主线程
	 */
	int THREAD_MAIN=0;

	/**
	 * 子线程
	 */
	int THREAD_BACKGROUND=2;

	/**
	 * 自动
	 */
	int THREAD_AUTO=-1;

	/**
	 * 接收消息的ID
	 *
	 * @return 消息ID
	 */
	int id();

	/**
	 * 是否是黏性消息
	 *
	 * @return true - 黏性消息<br/>
	 * false - 非黏性消息
	 */
	boolean sticky() default false;

	/**
	 * 优先级<br/>
	 * 数字越大优先级越高
	 *
	 * @return 优先级数值
	 */
	int priority() default 0;

	/**
	 * 线程类型
	 *
	 * @return 线程类型
	 */
	int threadType() default THREAD_AUTO;

	/**
	 * 是否是静态方法
	 *
	 * @return true - 静态方法<br/>
	 * false - 非静态方法
	 */
	boolean staticMethod() default false;
}