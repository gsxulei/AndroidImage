package com.x62.image;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.x62.base.ImageLoaderWrapper;
import com.x62.utils.SystemBarCompat;

import java.io.File;

public class ImagePreviewActivity extends AppCompatActivity
{
	private ImageView iv;
	private SubsamplingScaleImageView ssiv;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
		int FLAG_FULLSCREEN=WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setFlags(FLAG_FULLSCREEN,FLAG_FULLSCREEN);//设置全屏


		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_image_preview);
		//SystemBarCompat.tint(this);

//		getSupportActionBar().hide();
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View
				.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			getWindow().setNavigationBarColor(Color.TRANSPARENT);
		}

		iv=(ImageView)findViewById(R.id.iv);
		ssiv=(SubsamplingScaleImageView)findViewById(R.id.ssiv);
		//		//ssiv.setMinScale(0.8F);
		//		//ssiv.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);

		String path=getIntent().getStringExtra("path");

		ImageLoaderWrapper.Options options=new ImageLoaderWrapper.Options();
		options.activity=this;
		options.file=new File(path);
		options.iv=iv;
		options.isCenterCrop=false;
		options.placeholder=0;
		ImageLoaderWrapper.load(options);

		if(!TextUtils.isEmpty(path))
		{
			ssiv.setImage(ImageSource.uri(Uri.fromFile(new File(path))));
		}
		ssiv.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener()
		{
			@Override
			public void onReady()
			{
			}

			@Override
			public void onImageLoaded()
			{
				iv.setVisibility(View.GONE);
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
		});
	}
}