package commons.network;

import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;

import commons.base.BaseMsgEventId;
import commons.msgbus.MsgBus;
import commons.msgbus.MsgEvent;
import commons.msgbus.MsgReceiver;
import commons.utils.IOUtils;
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
	}

	@MsgReceiver(id=BaseMsgEventId.ID_10000000)
	public static void download(MsgEvent<String[]> event)
	{
		if(TextUtils.isEmpty(downloadDir))
		{
			throw new RuntimeException("请设置下载目录");
		}

		if(event.t==null||event.t.length==0||TextUtils.isEmpty(event.t[0]))
		{
			return;
		}
		String localPath=getLocalPathByUrl(event.t[0]);

		if(isDownload(event.t[0]))
		{
			notify(event,localPath);
			return;
		}

		OkHttpClient client=clientBuilder.build();

		Request.Builder reqBuilder=new Request.Builder();
		reqBuilder.url(event.t[0]);
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

				notify(event,localPath);
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
		//TODO 理论上这里存在哈希碰撞,但概率较小,暂时或略
		String name=url.hashCode()+"";
		File file=new File(downloadDir,name);
		return file.getAbsolutePath();
	}

	private static void notify(MsgEvent<String[]> event,String path)
	{
		MsgEvent<String[]> retEvent=new MsgEvent<>();
		retEvent.id=Integer.parseInt(event.t[1]);
		retEvent.t=new String[]{event.t[0],path};
		MsgBus.send(retEvent);
	}
}