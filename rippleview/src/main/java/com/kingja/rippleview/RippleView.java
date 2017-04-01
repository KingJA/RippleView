package com.kingja.rippleview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.AdapterView;
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
    private GestureDetector gestureDetector;

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent event) {
                Log.e("RippleView", "onLongPress: " );
                super.onLongPress(event);
                animateRipple(event);
                sendClickEvent(true);
            }


            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.e("RippleView", "onSingleTapConfirmed: " );
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.e("RippleView", "onSingleTapUp: " );
                return true;
            }

        });
        this.setDrawingCacheEnabled(true);
        this.setClickable(true);
        initAttr(context, attrs);
        initRippleView();



    }

    private int alpha = 90;

    private void initRippleView() {
        mRipplePaint = new Paint();
        mRipplePaint.setAntiAlias(true);
        mRipplePaint.setStyle(Paint.Style.FILL);
        mRipplePaint.setColor(0xffff0000);
        mRipplePaint.setAlpha(alpha);
    }

    private void initAttr(Context context, AttributeSet attrs) {

    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        this.onTouchEvent(event);
        return super.onInterceptTouchEvent(event);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            animateRipple(event);
            sendClickEvent(false);
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

    private void sendClickEvent(final Boolean isLongClick) {
        if (getParent() instanceof AdapterView) {
            final AdapterView adapterView = (AdapterView) getParent();
            final int position = adapterView.getPositionForView(this);
            final long id = adapterView.getItemIdAtPosition(position);
            if (isLongClick) {
                if (adapterView.getOnItemLongClickListener() != null)
                    adapterView.getOnItemLongClickListener().onItemLongClick(adapterView, this, position, id);
            } else {
                if (adapterView.getOnItemClickListener() != null)
                    adapterView.getOnItemClickListener().onItemClick(adapterView, this, position, id);
            }
        }
    }
}
