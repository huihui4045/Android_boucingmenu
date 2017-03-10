package com.huihui.boucingmenu;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class BouncingView extends View {
    private Paint mPaint;
    //�仯�Ĺ��̵��е�ǰ���ȵĸ߶�mArcHeight
    private int mArcHeight;//��ǰ�Ļ���
    private  int mMaxArcHeight;//�������߶�
    private  Status mStatus=Status.NONE;
    private  Path mPath = new Path();
	private AnimationListener animationListener;
    
    public enum Status{
        NONE,
        STATUS_SMOOTH_UP,
//        STATUS_UP,
        STATUS_DOWN,
    }
    
    public BouncingView(Context context) {
        super(context);
        init();
    }

    public BouncingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BouncingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(android.R.color.white));
//        mPaint.setColor(getResources().getColor(android.R.color.holo_red_dark));
        mMaxArcHeight=getResources().getDimensionPixelSize(R.dimen.arc_max_height);
    }

	public void show() {
		if(animationListener!=null){
			this.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					animationListener.showContent();
				}
			}, 600);
		}
		//������Ч
		mStatus = Status.STATUS_SMOOTH_UP;
		ValueAnimator valueAnimator = ValueAnimator.ofInt(0,mMaxArcHeight);
		valueAnimator.setDuration(800);
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mArcHeight = (int) animation.getAnimatedValue();
				if(mArcHeight==mMaxArcHeight){
					bounce();
				}
				invalidate();
			}
		});
		valueAnimator.start();
	}
	
	protected void bounce() {
		mStatus = Status.STATUS_DOWN;
		//������Ч
		ValueAnimator valueAnimator = ValueAnimator.ofInt(mMaxArcHeight,0);
		valueAnimator.setDuration(500);
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mArcHeight = (int) animation.getAnimatedValue();
				invalidate();
			}
		});
		valueAnimator.start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int currentPointY = 0;
		//���ϵؿ������y��ֵ�仯
		switch (mStatus) {
		case NONE:
			currentPointY = 0;
			break;
		case STATUS_SMOOTH_UP:
			/**currentPointY��ֵ---��mArcHeight�ı仯����һ���ġ�
			 * getHeight()~0		0~mMaxArcHeight
			 * currentPointY/getHeight = 1 - mArcHeight/mMaxArcHeight
			 */
			currentPointY = (int) (getHeight()*(1-(float)mArcHeight/mMaxArcHeight) + mMaxArcHeight);
			break;
		case STATUS_DOWN:
			currentPointY = mMaxArcHeight;
			break;
		}
		
		
		mPath.reset();
		mPath.moveTo(0, currentPointY);
		mPath.quadTo(getWidth()/2, currentPointY - mArcHeight, getWidth(), currentPointY);
		mPath.lineTo(getWidth(), getHeight());
		mPath.lineTo(0, getHeight());
		mPath.close();
		
		canvas.drawPath(mPath, mPaint);
	}

	public void setAnimationListener(AnimationListener listener){
		this.animationListener = listener;
	}
	public interface AnimationListener{
		void showContent();
	}
	
}
