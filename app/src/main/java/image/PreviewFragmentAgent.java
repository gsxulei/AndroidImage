package image;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.x62.image.R;
import commons.utils.MsgEventId;

import java.io.File;

import commons.annotations.LayoutBind;
import commons.agent.BaseAgent;
import commons.image.ImageLoaderWrapper;
import commons.msgbus.MsgBus;
import commons.msgbus.MsgEvent;
import commons.msgbus.MsgReceiver;
import commons.network.Downloader;
import commons.utils.ViewBind;

/**
 * 图片预览
 */
@LayoutBind(R.layout.commons_fragment_image_preview)
public class PreviewFragmentAgent extends BaseAgent implements SubsamplingScaleImageView.OnImageEventListener
{
	@ViewBind.Bind(id=R.id.cover)
	private ImageView mCover;

	@ViewBind.Bind(id=R.id.zoom_image)
	private SubsamplingScaleImageView mZoomImage;

	private String path;

	public void setPath(String path)
	{
		this.path=path;
	}

	@Override
	public void initData()
	{
		if(TextUtils.isEmpty(path)||mZoomImage==null)
		{
			return;
		}

//		Downloader.Options downloader=new Downloader.Options();
//		downloader.url=path;
//		downloader.successId=MsgEventId.ID_200031;
//		downloader.failId=MsgEventId.ID_200032;

		ImageLoaderWrapper.Options<Context> options=new ImageLoaderWrapper.Options<>();
		options.obj=mContext;
		options.path=path;
		options.iv=mCover;
		options.isCenterCrop=false;
		options.placeholder=0;
		//options.downloader=downloader;
		ImageLoaderWrapper.load(options);

		//当图片为网络图片且未下载时不显示大图
		if(path.startsWith("http")&&!Downloader.isDownload(path))
		{
			return;
		}
		File file=new File(path);
		if(path.startsWith("http"))
		{
			file=new File(Downloader.getLocalPathByUrl(path));
		}
		mZoomImage.setImage(ImageSource.uri(Uri.fromFile(file)));
		mZoomImage.setOnImageEventListener(this);
	}

	@MsgReceiver(id=MsgEventId.ID_200031)
	void downloadImage(MsgEvent<String> event)
	{
		if(path.equals(event.t))
		{
			initData();
			MsgBus.cancel(event);
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(mZoomImage!=null)
		{
			mZoomImage.recycle();
		}
	}

	@Override
	public void onReady()
	{
	}

	@Override
	public void onImageLoaded()
	{
		mCover.setVisibility(View.GONE);
	}

	@Override
	public void onPreviewLoadError(Exception e)
	{
	}

	@Override
	public void onImageLoadError(Exception e)
	{
	}

	@Override
	public void onTileLoadError(Exception e)
	{
	}

	@Override
	public void onPreviewReleased()
	{
	}
}