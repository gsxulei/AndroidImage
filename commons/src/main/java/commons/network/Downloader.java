package commons.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import commons.utils.IOUtils;
import commons.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class Downloader
{
	private static String downloadDir;
	private static OkHttpClient.Builder clientBuilder=new OkHttpClient.Builder();
	private static final ExecutorService POOL=Executors.newCachedThreadPool();

	static
	{
		HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

		clientBuilder.retryOnConnectionFailure(true);
		clientBuilder.addNetworkInterceptor(interceptor);
	}

	private static Handler handler=new Handler(Looper.getMainLooper())
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			Options option=(Options)msg.obj;
			if(msg.what==1)
			{
				option.callBack.onDownloadSuccess(getLocalPathByUrl(option.url));
			}
			else if(msg.what==2)
			{
				option.callBack.onDownloadFail();
			}
		}
	};

	private static void download(String url) throws Exception
	{
		String localPath=getLocalPathByUrl(url);
		OkHttpClient client=clientBuilder.build();

		Request.Builder reqBuilder=new Request.Builder();
		reqBuilder.url(url);
		reqBuilder.get();

		Call call=client.newCall(reqBuilder.build());
		Response response=call.execute();

		FileOutputStream fos=new FileOutputStream(localPath);

		ResponseBody body=response.body();
		if(body!=null)
		{
			IOUtils.copy(body.byteStream(),fos);
		}
		else
		{
			throw new Exception();
		}
	}

	public static void download(final Options option)
	{
		if(TextUtils.isEmpty(downloadDir))
		{
			try
			{
				throw new Exception("请设置下载目录");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		if(TextUtils.isEmpty(option.url)||option.callBack==null)
		{
			return;
		}

		POOL.submit(new Runnable()
		{
			@Override
			public void run()
			{
				Message message=new Message();
				message.what=1;
				message.obj=option;
				try
				{
					download(option.url);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					message.what=2;
				}
				finally
				{
					handler.sendMessage(message);
				}
			}
		});
	}

	/**
	 * 设置下载目录
	 *
	 * @param dir 下载目录
	 */
	public static void setDownloadDir(String dir)
	{
		downloadDir=dir;
	}

	/**
	 * 文件是否已下载
	 *
	 * @param url 文件Url
	 * @return true 已下载<br/>
	 * false 未下载
	 */
	public static boolean isDownload(String url)
	{
		File file=new File(getLocalPathByUrl(url));
		return file.exists();
	}

	/**
	 * 获取文件下载保存本地的路径
	 *
	 * @param url 文件Url
	 * @return 文件下载保存本地的路径
	 */
	public static String getLocalPathByUrl(String url)
	{
		String md5=Utils.md5(url);
		File file=new File(downloadDir,md5);
		return file.getAbsolutePath();
	}

	public interface CallBack
	{
		void onDownloadSuccess(String localPath);

		void onDownloadFail();
	}

	public static class DefaultDownloadCallBack implements CallBack
	{
		@Override
		public void onDownloadSuccess(String localPath)
		{
		}

		@Override
		public void onDownloadFail()
		{
		}
	}

	public static class Options
	{
		public String url;
		public CallBack callBack;
	}
}