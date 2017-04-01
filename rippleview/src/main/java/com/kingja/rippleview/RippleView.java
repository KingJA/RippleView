package com.kingja.rippleview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Description:TODO
 * Create Time:2017/4/1 10:26
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class RippleView extends RelativeLayout {

    private final String TAG = getClass().getSimpleName();
    private float x;
    private float y;
    private Paint mRipplePaint;

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initRippleView();
        setWillNotDraw(false);
    }

    private int alpha = 90;

    private void initRippleView() {
        this.setClickable(true);
        mRipplePaint = new Paint();
        mRipplePaint.setAntiAlias(true);
        mRipplePaint.setStyle(Paint.Style.FILL);
        mRipplePaint.setColor(0xffff0000);
        mRipplePaint.setAlpha(alpha);
    }

    private void initAttr(Context context, AttributeSet attrs) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            animateRipple(event);
        }
        return super.onTouchEvent(event);
    }

    private void animateRipple(MotionEvent event) {
        if (running) {
            return;
        }
        x = event.getX();
        y = event.getY();
        startAnimator();
    }

    private Handler canvasHandler = new Handler();
    private boolean running;
    private float radius = 100;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRipplePaint.setAlpha((int) (alpha - alpha * (currentValue / radius)));
        canvas.drawCircle(x, y, currentValue, mRipplePaint);

    }

    private float currentValue;

    public void startAnimator() {

        ValueAnimator anim = ValueAnimator.ofFloat(0f, radius);
        anim.setDuration(1000);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                running = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                running = false;
                mRipplePaint.setAlpha(alpha);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = Math.max(getMeasuredWidth(), getMeasuredHeight());
    }
}
