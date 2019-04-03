package commons.msgbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消息接收者
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MsgReceiver
{
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
	int threadType() default MsgThread.BACKGROUND;
}