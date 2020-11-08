package commons.image;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

/**
 * 图片加载器封装
 */
public class ImageLoaderWrapper
{
	public static int defaultPlaceholder;
	public static int defaultError;

	public static void setDefault(int id)
	{
		defaultPlaceholder=id;
		defaultError=id;
	}

	/**
	 * 参数选项
	 */
	public static class Options<T>
	{
		public T obj;
		public String path;
		public ImageView iv;
		public int placeholder=defaultPlaceholder;
		public int error=defaultError;
		public boolean isCenterCrop=true;
	}

	public static void load(Options<?> options)
	{
		glideLoad(options);
	}

	/**
	 * 使用Glide加载图片
	 *
	 * @param options 参数选项
	 */
	private static void glideLoad(final Options<?> options)
	{
		RequestManager manager=null;
		if(options.obj instanceof Activity)
		{
			Activity activity=(Activity)options.obj;
			if(activity.isDestroyed()||activity.isFinishing())
			{
				return;
			}
			manager=Glide.with(activity);
		}
		else if(options.obj instanceof Fragment)
		{
			Activity activity=((Fragment)options.obj).getActivity();
			if(activity.isDestroyed()||activity.isFinishing())
			{
				return;
			}
			manager=Glide.with((Fragment)options.obj);
		}
		else if(options.obj instanceof android.support.v4.app.Fragment)
		{
			Activity activity=((android.support.v4.app.Fragment)options.obj).getActivity();
			if(activity.isDestroyed()||activity.isFinishing())
			{
				return;
			}
			manager=Glide.with((android.support.v4.app.Fragment)options.obj);
		}
		else if(options.obj instanceof Context)
		{
			manager=Glide.with((Context)options.obj);
		}

		if(manager==null)
		{
			return;
		}

		final RequestOptions reqOptions=new RequestOptions();
		if(options.placeholder>0)
		{
			reqOptions.placeholder(options.placeholder);
		}
		if(options.isCenterCrop)
		{
			reqOptions.centerCrop();
		}
		reqOptions.error(options.error);

		if(TextUtils.isEmpty(options.path))
		{
			return;
		}

		manager.load(options.path).apply(reqOptions).into(options.iv);
	}
}