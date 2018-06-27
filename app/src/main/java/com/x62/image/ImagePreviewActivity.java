package com.x62.image;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
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

	private int width=-1;
	private int height=-1;
	private int x=0;
	private int y=0;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		//requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题

		super.onCreate(savedInstanceState);

		//		Window window=getWindow();
		//		Transition transition=null;
		//		window.setEnterTransition(transition);

		setContentView(R.layout.activity_image_preview);
		SystemBarCompat.imagePreviewMode(this);

		//overridePendingTransition(0,0);

		iv=(ImageView)findViewById(R.id.iv);
		ssiv=(SubsamplingScaleImageView)findViewById(R.id.ssiv);
		//ssiv.setMaxScale(3.0F);
		//ssiv.setMinScale(0.8F);
		//ssiv.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
		AnimationUtils animationUtils=null;
		//animationUtils.
		Animation animation=null;
		//animation.s

		String path="";
		Intent intent=getIntent();
		if(intent!=null)
		{
			width=intent.getIntExtra("width",-1);
			height=intent.getIntExtra("height",-1);
			x=intent.getIntExtra("x",0);
			y=intent.getIntExtra("y",0);
			path=intent.getStringExtra("path");
		}
		Log.e("xulei","width->"+width);
		Log.e("xulei","height->"+height);
		Log.e("xulei","x->"+x);
		Log.e("xulei","y->"+y);

		float pivotX=x+width/2;
		if(x!=0)
		{
			//pivotX=x+width/2-40;
		}
		float pivotY=y+height/2;

		Log.e("xulei","pivotX->"+pivotX);
		Log.e("xulei","pivotY->"+pivotY);

		View view=findViewById(getWindow().ID_ANDROID_CONTENT);
		ScaleAnimation scaleAnimation=new ScaleAnimation(((float)width)/1080,1.0f,((float)height)/1812,1.0f,pivotX,
				pivotY);
		scaleAnimation.setDuration(6000);
		view.startAnimation(scaleAnimation);
		Log.e("xulei","fromX->"+view.getWidth());

		ImageLoaderWrapper.Options<Activity> options=new ImageLoaderWrapper.Options();
		options.obj=this;
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

	@Override
	public void finish()
	{
		ScaleAnimation scaleAnimation=new ScaleAnimation(1.0f,((float)width)/1080,1.0f,((float)height)/1812,x+width/2,
				y+height/2);
		scaleAnimation.setDuration(200);
		scaleAnimation.setAnimationListener(new Animation.AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				ImagePreviewActivity.super.finish();
				overridePendingTransition(0,0);
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}
		});
		View view=findViewById(getWindow().ID_ANDROID_CONTENT);
		view.startAnimation(scaleAnimation);
	}
}