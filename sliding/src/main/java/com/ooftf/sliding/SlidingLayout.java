package com.ooftf.sliding;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author ooftf
 * @date 2018/8/23 0023
 * @desc
 **/
public class SlidingLayout extends FrameLayout {
    int openHeight = 0;
    int closeHeight = 0;
    int currentHeight = 0;
    ScrollerPlus scrollerPlus;
    CompositeDisposable disposables = new CompositeDisposable();
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
        scrollerPlus = new ScrollerPlus(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        closeHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (currentHeight == 0) {
            currentHeight = closeHeight;
        }
        int boundless = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, 0, boundless, 0);
            openHeight = Math.max(child.getMeasuredHeight(), openHeight);
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), currentHeight);
    }

    int duration = 600;

    public void smoothOpen() {
        isOpen = true;
        startScroll(currentHeight, openHeight - currentHeight);

    }

    public void smoothClose() {
        isOpen = false;
        startScroll(currentHeight, closeHeight - currentHeight);
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

    private void startScroll(int startY, int dy) {
        disposables.add(scrollerPlus
                .startScrollRx(0, startY, 0, dy, duration)
                .map(new Function<Point, Integer>() {

                    @Override
                    public Integer apply(Point point) throws Exception {
                        return point.y;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer y) throws Exception {
                        currentHeight = y;
                        requestLayout();
                    }
                }));
    }

    @Override
    protected void onDetachedFromWindow() {
        disposables.dispose();
        super.onDetachedFromWindow();
    }
}
