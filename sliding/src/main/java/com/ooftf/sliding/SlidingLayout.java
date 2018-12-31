package com.ooftf.sliding;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

/**
 * @author ooftf
 * @date 2018/8/23 0023
 * @desc
 **/
public class SlidingLayout extends FrameLayout {
    int openHeight = 0;
    int closeHeight = 0;
    int currentHeight = 0;
    ValueAnimator animator;
    boolean isOpen = false;

    public SlidingLayout(Context context) {
        super(context);
        init();
    }


    public SlidingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlidingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlidingLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        animator = new ValueAnimator();
        animator.setDuration(duration);
        animator.setInterpolator(new ViscousFluidInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                currentHeight = (int) value;
                requestLayout();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 以固定高度为默认高度
        closeHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 以无限高度默认测量子view的高度
        int boundless = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, 0, boundless, 0);
            openHeight = Math.max(child.getMeasuredHeight(), openHeight);
        }
        // 如果打开状态高度小于关闭状态高度，那么将改关闭和打开设置为同一高度
        if (openHeight < closeHeight) {
            closeHeight = openHeight;
        }
        //初始状态设置为关闭状态
        if (!animator.isRunning()) {
            if (isOpen) {
                currentHeight = openHeight;
            } else {
                currentHeight = closeHeight;
            }
        }

        // 设置高度
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), currentHeight);
    }

    int duration = 600;

    public void smoothOpen() {
        isOpen = true;
        startScroll(currentHeight, openHeight);

    }

    public void smoothClose() {
        isOpen = false;
        startScroll(currentHeight, closeHeight);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void smoothTurn() {
        if (isOpen) {
            smoothClose();
        } else {
            smoothOpen();
        }
    }

    private void startScroll(int startY, int endy) {
        animator.setFloatValues(startY, endy);
        animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        animator.end();
        super.onDetachedFromWindow();
    }

    static class ViscousFluidInterpolator implements Interpolator {
        /**
         * Controls the viscous fluid effect (how much of it).
         */
        private static final float VISCOUS_FLUID_SCALE = 8.0f;

        private static final float VISCOUS_FLUID_NORMALIZE;
        private static final float VISCOUS_FLUID_OFFSET;

        static {

            // must be set to 1.0 (used in viscousFluid())
            VISCOUS_FLUID_NORMALIZE = 1.0f / viscousFluid(1.0f);
            // account for very small floating-point error
            VISCOUS_FLUID_OFFSET = 1.0f - VISCOUS_FLUID_NORMALIZE * viscousFluid(1.0f);
        }

        private static float viscousFluid(float x) {
            x *= VISCOUS_FLUID_SCALE;
            if (x < 1.0f) {
                x -= (1.0f - (float) Math.exp(-x));
            } else {
                float start = 0.36787944117f;   // 1/e == exp(-1)
                x = 1.0f - (float) Math.exp(1.0f - x);
                x = start + x * (1.0f - start);
            }
            return x;
        }

        @Override
        public float getInterpolation(float input) {
            final float interpolated = VISCOUS_FLUID_NORMALIZE * viscousFluid(input);
            if (interpolated > 0) {
                return interpolated + VISCOUS_FLUID_OFFSET;
            }
            return interpolated;
        }
    }
}
