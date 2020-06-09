package com.ooftf.sliding;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

/**
 * @author ooftf
 * @date 2018/8/23 0023
 * @desc
 **/
public class SlidingLayout extends FrameLayout {
    float openHeight = 0;
    float closeHeight = 0;
    float currentHeight = 0;
    ValueAnimator animator;
    boolean isOpen = false;

    public SlidingLayout(Context context) {
        super(context);
        init();
    }


    public SlidingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainAttrs(attrs);
        init();
    }


    public SlidingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainAttrs(attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlidingLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        obtainAttrs(attrs);
        init();
    }

    boolean isShelterMode = true;

    private void obtainAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SlidingLayout);
        isShelterMode = typedArray.getBoolean(R.styleable.SlidingLayout_sl_isShelterMode, true);
        closeHeight = typedArray.getDimension(R.styleable.SlidingLayout_sl_closeHeight, 0);
        typedArray.recycle();
    }

    private float getCloseHeight() {
        // 如果打开状态高度小于关闭状态高度，那么将改关闭和打开设置为同一高度
        float openHeight = getOpenHeight();
        if (closeHeight > openHeight) {
            return openHeight;
        }
        return closeHeight;
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

        // post(OnMe)
    }

    int mWidthMeasureSpec = 0;
    int unspecifiedHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidthMeasureSpec = widthMeasureSpec;
        // 以无限高度默认测量子view的高度
        int realHeight = 0;
        if (!isShelterMode) {
            realHeight = MeasureSpec.makeMeasureSpec((int) currentHeight, MeasureSpec.EXACTLY);
        }
        openHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            measureChildCompat(child, widthMeasureSpec, unspecifiedHeight);
            openHeight = Math.max(child.getMeasuredHeight(), openHeight);
            if (!isShelterMode) {
                measureChildCompat(child, widthMeasureSpec, realHeight);
            }
        }
        // 如果打开状态高度小于关闭状态高度，那么将改关闭和打开设置为同一高度

        //初始状态设置为关闭状态
        if (!animator.isRunning()) {
            if (isOpen) {
                currentHeight = openHeight;
            } else {
                currentHeight = getCloseHeight();
            }
        }
        Log.e("currentHeight", "" + currentHeight);
        // 设置高度
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (int) currentHeight);
        //super.onMeasure(widthMeasureSpec,MeasureSpec.makeMeasureSpec((int) currentHeight, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
        }
    }

    /**
     * 如果内容小于关闭高度，那么大小会设置为内容高度，打开关闭不会有变化
     *
     * @return
     */
    public boolean isWillChange() {
        return getOpenHeight() > closeHeight;
    }

    float getOpenHeight() {
        openHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChildCompat(child, mWidthMeasureSpec, unspecifiedHeight);
            openHeight = Math.max(child.getMeasuredHeight(), openHeight);
        }
        return openHeight;
    }

    void measureChildCompat(View child, int widthMeasureSpec, int heightMeasureSpec) {
        if (child.getLayoutParams() instanceof MarginLayoutParams) {
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
        } else {
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setAnimatorChangeListener(Animator.AnimatorListener listener) {
        animator.addListener(listener);
    }

    int duration = 600;

    public void smoothOpen() {
        isOpen = true;
        startScroll(currentHeight, getOpenHeight());

    }

    public void smoothClose() {
        isOpen = false;
        Log.e("currentHeight", currentHeight + "::" + closeHeight);
        startScroll(currentHeight, getCloseHeight());
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

    private void startScroll(float startY, float endY) {
        animator.setFloatValues(startY, endY);
        animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        animator.cancel();
        super.onDetachedFromWindow();
    }


    /**
     * 从Scroller中偷来的插值器
     */
    public static class ViscousFluidInterpolator implements Interpolator {
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
                float start = 0.36787944117f;
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
