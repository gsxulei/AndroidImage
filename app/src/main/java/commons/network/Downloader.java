package commons.network;

import android.text.TextUtils;

import commons.utils.MsgEventId;

import java.io.File;
import java.io.FileOutputStream;

import commons.msgbus.MsgBus;
import commons.msgbus.MsgEvent;
import commons.msgbus.MsgReceiver;
import commons.utils.IOUtils;
import commons.utils.Utils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 文件下载
 */
public class Downloader
{
	private static String downloadDir;
	private static OkHttpClient.Builder clientBuilder=new OkHttpClient.Builder();

	static
	{
		HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

		clientBuilder.retryOnConnectionFailure(true);
		clientBuilder.addNetworkInterceptor(interceptor);
		clientBuilder.retryOnConnectionFailure(true);
	}

	@MsgReceiver(id=MsgEventId.ID_200030)
	public static void download(MsgEvent<Options> event)
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

		Options opt=event.t;
		if(TextUtils.isEmpty(opt.url))
		{
			return;
		}
		String localPath=getLocalPathByUrl(opt.url);
		OkHttpClient client=clientBuilder.build();

		Request.Builder reqBuilder=new Request.Builder();
		reqBuilder.url(opt.url);
		reqBuilder.get();

		try
		{
			Call call=client.newCall(reqBuilder.build());
			Response response=call.execute();

			FileOutputStream fos=new FileOutputStream(localPath);

			ResponseBody body=response.body();
			if(body!=null)
			{
				IOUtils.copy(body.byteStream(),fos);

				MsgEvent<String> successEvent=new MsgEvent<>();
				successEvent.id=opt.successId;
				successEvent.t=opt.url;
				MsgBus.send(successEvent);
			}
			else
			{
				throw new Exception();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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

	public static class Options
	{
		public String url;
		public int successId;
		public int failId;
	}
}