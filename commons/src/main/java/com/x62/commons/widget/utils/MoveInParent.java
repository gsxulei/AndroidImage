package com.x62.commons.widget.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * 在父控件内拖动子控件
 */
public class MoveInParent implements View.OnTouchListener
{
	/**
	 * 父控件
	 */
	private View mParent;

	/**
	 * 是否拖动
	 */
	private boolean isMove=false;

	/**
	 * 是否已经在拖动
	 */
	private boolean isMoved=false;

	/**
	 * 上次位置X坐标
	 */
	private int lastX;

	/**
	 * 上次位置Y坐标
	 */
	private int lastY;

	public void setView(View view)
	{
		if(view==null)
		{
			return;
		}
		mParent=(View)view.getParent();
		view.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(final View v,MotionEvent event)
	{
		int x=(int)event.getX();
		int y=(int)event.getY();

		switch(event.getAction())
		{
			//手指按下
			case MotionEvent.ACTION_DOWN:
			{
				lastX=x;
				lastY=y;
				isMove=false;
				isMoved=false;
			}
			break;

			//手指移动
			case MotionEvent.ACTION_MOVE:
			{
				int offsetX=x-lastX;
				int offsetY=y-lastY;

				if(this.isMoved)
				{
					isMove=true;
				}
				else
				{
					//小于10像素的不算拖动
					if((Math.abs(offsetX)<10)||(Math.abs(offsetY)<10))
					{
						isMoved=false;
						break;
					}
					isMove=true;
					isMoved=true;
				}
				if(v.getLeft()+offsetX<mParent.getLeft())
				{
					offsetX=mParent.getLeft()-v.getLeft();
				}
				if(v.getRight()+offsetX>mParent.getRight())
				{
					offsetX=mParent.getRight()-v.getRight();
				}
				if(v.getTop()+offsetY<mParent.getTop())
				{
					offsetY=mParent.getTop()-v.getTop();
				}
				if(v.getBottom()+offsetY>mParent.getBottom())
				{
					offsetY=mParent.getBottom()-v.getBottom();
				}
				v.offsetLeftAndRight(offsetX);
				v.offsetTopAndBottom(offsetY);
			}
			break;

			case MotionEvent.ACTION_UP:
			{
				//拖动结束时,当控件在父控件中偏左则将控件移动到父控件最左边
				//当控件在父控件中偏右则将控件移动到父控件最右边
				if(isMove)
				{
					int dx=mParent.getLeft()-v.getLeft();
					if(v.getRight()-v.getWidth()/2>mParent.getWidth()/2)
					{
						dx=mParent.getRight()-v.getRight();
					}
					final int offset=dx;
					TranslateAnimation animation=new TranslateAnimation(0.0F,offset,0.0F,0.0F);
					animation.setDuration(200L);
					animation.setFillAfter(true);
					animation.setAnimationListener(new Animation.AnimationListener()
					{
						public void onAnimationEnd(Animation paramAnonymousAnimation)
						{
							v.offsetLeftAndRight(offset);
							v.clearAnimation();
						}

						public void onAnimationRepeat(Animation paramAnonymousAnimation)
						{
						}

						public void onAnimationStart(Animation paramAnonymousAnimation)
						{
						}
					});
					v.startAnimation(animation);
				}
				else
				{
					v.performClick();
				}
			}
			break;
		}
		return isMove;
	}
}