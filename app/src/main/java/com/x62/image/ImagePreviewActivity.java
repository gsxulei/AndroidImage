package com.x62.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.x62.base.ImageLoaderWrapper;
import com.x62.utils.ScreenUtils;
import com.x62.utils.SystemBarCompat;
import com.x62.utils.ViewBind;

import java.io.File;

/**
 * 图片预览
 */
public class ImagePreviewActivity extends AppCompatActivity
{
	@ViewBind.Bind(id=R.id.iv)
	private ImageView iv;

	@ViewBind.Bind(id=R.id.ssiv)
	private SubsamplingScaleImageView ssiv;

	@ViewBind.Bind(id=R.id.flImage)
	private FrameLayout flImage;

	private int width=-1;
	private int height=-1;
	private int x=0;
	private int y=0;
	private float fromX;
	private float fromY;

	private float screenWidth=1080F;
	private float screenHeight=1920F;

	private ScreenUtils screenUtils=ScreenUtils.getInstance();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题

		super.onCreate(savedInstanceState);

		//		Window window=getWindow();
		//		Transition transition=null;
		//		window.setEnterTransition(transition);
		SystemBarCompat.imagePreviewMode(this);
		setContentView(R.layout.activity_image_preview);
		//initStatusBar();

		ViewBind.bind(this);
		//overridePendingTransition(0,0);
		screenWidth=screenUtils.getWidth();
		screenHeight=screenUtils.getHeight();

		//iv=(ImageView)findViewById(R.id.iv);
		//ssiv=(SubsamplingScaleImageView)findViewById(R.id.ssiv);
		//ssiv.setMaxScale(3.0F);
		//ssiv.setMinScale(0.8F);
		//ssiv.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
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

		BitmapFactory.Options opt=new BitmapFactory.Options();
		opt.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(path,opt);

		//		Log.e("xulei","width->"+width);
		//		Log.e("xulei","height->"+height);
		//		Log.e("xulei","x->"+x);
		//		Log.e("xulei","y->"+y);
		Log.e("xulei","outWidth->"+opt.outWidth);
		Log.e("xulei","outHeight->"+opt.outHeight);

		float realWidth=1F;
		float realHeight=1F;
		float imgProportion=1F;
		float scaleHeight=1F;
		if(opt.outWidth>opt.outHeight)
		{
			imgProportion=((float)opt.outWidth)/opt.outHeight;
			realWidth=screenWidth;
			realHeight=opt.outHeight/(opt.outWidth/realWidth);

			scaleHeight=screenHeight*height/realHeight;
			fromX=width/screenWidth;
			fromY=scaleHeight/screenHeight;
			y=y-((int)scaleHeight-width)/2;
		}
		else
		{
			realWidth=screenWidth;
			realHeight=opt.outHeight/(opt.outWidth/realWidth);

			scaleHeight=screenHeight*height/realHeight;
			fromX=width/screenWidth;
			fromY=scaleHeight/screenHeight;
			y=y-((int)scaleHeight-width)/2;
		}

		Log.e("xulei","realWidth->"+realWidth);
		Log.e("xulei","realHeight->"+realHeight);
		Log.e("xulei","scaleHeight->"+scaleHeight);

		//View view=findViewById(getWindow().ID_ANDROID_CONTENT);
		ScaleAnimation scaleAnimation=new ScaleAnimation(fromX,1.0f,fromY,1.0f);
		TranslateAnimation translateAnimation=new TranslateAnimation(x,0,y,0);

		AnimationSet animation=new AnimationSet(true);
		animation.setDuration(200);
		animation.addAnimation(scaleAnimation);
		animation.addAnimation(translateAnimation);
		animation.setAnimationListener(new Animation.AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				flImage.setBackgroundColor(Color.BLACK);
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}
		});

		//view.startAnimation(scaleAnimation);
		//Log.e("xulei","fromX->"+view.getWidth());
		flImage.startAnimation(animation);

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

	public void initStatusBar()
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			int resourceId=getResources().getIdentifier("status_bar_height","dimen","android");
			// 获取状态栏高度
			int statusBarHeight=getResources().getDimensionPixelSize(resourceId);
			View rectView=new View(this);
			// 绘制一个和状态栏一样高的矩形，并添加到视图中
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					statusBarHeight);
			rectView.setLayoutParams(params);
			//设置状态栏颜色（该颜色根据你的App主题自行更改）
			rectView.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
			// 添加矩形View到布局中
			ViewGroup decorView=(ViewGroup)getWindow().getDecorView();
			decorView.addView(rectView);
			ViewGroup rootView=(ViewGroup)((ViewGroup)this.findViewById(android.R.id.content)).getChildAt(0);
			rootView.setFitsSystemWindows(true);
			rootView.setClipToPadding(true);
		}
	}

	@Override
	public void finish()
	{
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams
				.FLAG_FORCE_NOT_FULLSCREEN);
		flImage.setBackgroundColor(Color.TRANSPARENT);
		ScaleAnimation scaleAnimation=new ScaleAnimation(1.0f,fromX,1.0f,fromY);
		TranslateAnimation translateAnimation=new TranslateAnimation(0,x,0,y);

		AnimationSet animation=new AnimationSet(true);
		animation.setDuration(200);
		animation.addAnimation(scaleAnimation);
		animation.addAnimation(translateAnimation);
		animation.setAnimationListener(new Animation.AnimationListener()
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
		flImage.startAnimation(animation);

		//		ScaleAnimation scaleAnimation=new ScaleAnimation(1.0f,((float)width)/1080,1.0f,((float)height)/1812,
		// x+width/2,
		//				y+height/2);
		//		scaleAnimation.setDuration(200);
		//		scaleAnimation.setAnimationListener(new Animation.AnimationListener()
		//		{
		//			@Override
		//			public void onAnimationStart(Animation animation)
		//			{
		//			}
		//
		//			@Override
		//			public void onAnimationEnd(Animation animation)
		//			{
		//				ImagePreviewActivity.super.finish();
		//				overridePendingTransition(0,0);
		//			}
		//
		//			@Override
		//			public void onAnimationRepeat(Animation animation)
		//			{
		//			}
		//		});
		//		View view=findViewById(getWindow().ID_ANDROID_CONTENT);
		//		view.startAnimation(scaleAnimation);
	}
}