package com.cy.camerapreview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2016/4/15.
 */
public class RectSurfaceView extends SurfaceView {

    Paint mPaint;
    float clickX;
    float clickY;
    boolean showRect;

    public RectSurfaceView(Context context) {
        super(context);
        mPaint = new Paint();
        showRect = true;
    }

    public RectSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        showRect = true;
    }

    public RectSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        showRect = true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(getResources().getColor(android.R.color.holo_blue_light));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        if(showRect){
            // 矩形
            canvas.drawRect(clickX - 50, clickY - 50, clickX + 50, clickY + 50, mPaint);
            // 左
            canvas.drawLine(clickX - 50, clickY, clickX - 35, clickY, mPaint);
            // 上
            canvas.drawLine(clickX, clickY - 50, clickX, clickY - 35, mPaint);
            // 右
            canvas.drawLine(clickX + 50, clickY, clickX + 35, clickY, mPaint);
            // 下
            canvas.drawLine(clickX, clickY + 50, clickX, clickY + 35, mPaint);
            showRect = false;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            clickX = event.getX();
            clickY = event.getY();

            showRect = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RectSurfaceView.this.invalidate();
                }
            }, 800);
            invalidate();
        }
        return super.onTouchEvent(event);
    }
}
