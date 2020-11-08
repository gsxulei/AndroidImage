package commons.network;

import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import commons.base.BaseMsgEventId;
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
	private static final List<Integer> ids=new ArrayList<>();

	static
	{
		HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

		clientBuilder.retryOnConnectionFailure(true);
		clientBuilder.addNetworkInterceptor(interceptor);
	}

	@MsgReceiver(id=BaseMsgEventId.ID_10000000)
	public static void download(MsgEvent<String> event)
	{
		if(TextUtils.isEmpty(downloadDir))
		{
			throw new RuntimeException("请设置下载目录");
		}

		if(TextUtils.isEmpty(event.t))
		{
			return;
		}
		String localPath=getLocalPathByUrl(event.t);

		if(new File(localPath).exists())
		{
			notify(event.t,localPath);
			return;
		}

		OkHttpClient client=clientBuilder.build();

		Request.Builder reqBuilder=new Request.Builder();
		reqBuilder.url(event.t);
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

				notify(event.t,localPath);
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
		return file.exists()&&file.length()>0;
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

	private static void notify(String url,String path)
	{
		for(Integer id : ids)
		{
			MsgEvent<String[]> event=new MsgEvent<>();
			event.id=id;
			event.t=new String[]{url,path};
			MsgBus.send(event);
		}
	}

	public static void addNotifyId(int id)
	{
		ids.add(id);
	}
}