package commons.model;

public interface ModelCallBack<S,F>
{
	/**
	 * 成功回调
	 * 
	 * @param s
	 */
	void onSuccess(S s);

	/**
	 * 失败回调
	 * 
	 * @param f
	 */
	void onFail(F f);
}