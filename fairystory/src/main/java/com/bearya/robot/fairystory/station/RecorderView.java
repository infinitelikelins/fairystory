package com.bearya.robot.fairystory.station;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import com.bearya.robot.fairystory.R;


public class RecorderView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    float centerX = 360;
    float centerY = 360;
    float gapStartDeg = 270;
    private Path mPath;
    private RectF rect;
    private Bitmap circleBitmap;
    private Bitmap micBitmap;
    private int flag = -1;
    private ValueAnimator animator;
    private Rect micSrc = new Rect();
    private Rect micDst = new Rect();
    private Rect circleSrc = new Rect();
    private Rect circleDst = new Rect();
    private ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int angle = (int) valueAnimator.getAnimatedValue();
            mPath.reset();
            mPath.moveTo(centerX, centerY);
            mPath.arcTo(rect, gapStartDeg, angle*flag, false);
            if(angle<=1){
                flag =1;
            }
            if(angle>=359){
                flag = -1;
            }
            invalidate();
        }
    };

    public RecorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w/2;
        centerY = h/2;
        int padding = 5;
        micDst.set(padding,padding,w-padding,h-padding);
        circleDst.set(0,0,w,h);
        invalidate();
    }

    public RecorderView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RecorderView(Context context) {
        this(context,null);
    }

    private void init(){
        circleBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.station_record_circle);
        micBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_mic);
        micSrc.set(0,0,micBitmap.getWidth(),micBitmap.getHeight());
        circleSrc.set(0,0,circleBitmap.getWidth(),circleBitmap.getHeight());
        float radius = circleBitmap.getHeight();
        centerX = radius/2;
        centerY = radius/2;
        rect = new RectF(centerX - radius, centerY - radius, centerX + radius,centerY + radius);
    }


    public void startProgress(){
        mPath = new Path();
        animator = ObjectAnimator.ofInt(359,0);
        animator.setDuration(5000);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(updateListener);
        animator.start();

    }

    public void stopProgress(){
        if(animator!=null){
            animator.removeUpdateListener(updateListener);
            animator.cancel();
        }
        if(mPath!=null) {
            mPath.reset();
            mPath = null;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawBitmap(micBitmap,micSrc, micDst,mPaint);
        if(mPath!=null) {
            canvas.clipPath(mPath, Region.Op.DIFFERENCE);
            canvas.drawBitmap(circleBitmap, circleSrc, circleDst, mPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopProgress();
    }
}
