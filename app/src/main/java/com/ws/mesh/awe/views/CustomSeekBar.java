package com.ws.mesh.awe.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ws.mesh.awe.R;


/**
 * Created by louhon on 2015/4/20.
 */
public class CustomSeekBar extends ViewGroup implements View.OnTouchListener {

    public interface OnPositionChangedListener {
        void onNewPosition(float position, boolean isUp);
    }

    private OnPositionChangedListener onPositionChangedListener;

    public void setOnPositionChangedListener(OnPositionChangedListener onPositionChangedListener) {
        this.onPositionChangedListener = onPositionChangedListener;
    }

    private static final String TAG = CustomSeekBar.class.getSimpleName();

    private ImageView ivBackground;
    private ColorCircleView ccvThumb;

    public void setThumbColor(int color){
        ccvThumb.setInnerCircleColor(color);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        ccvThumb = new ColorCircleView(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomSeekBar,
                0, 0);
        int backgroundDrawable = 0;
        try {
            backgroundDrawable = a.getResourceId(R.styleable.CustomSeekBar_csBackground, 0);
            HEIGHT_MULTIPLEXER = a.getInteger(R.styleable.CustomSeekBar_csMultiplexer, 7);
            int thumbColor = a.getColor(R.styleable.CustomSeekBar_csThumbStartColor, Color.RED);
            ccvThumb.setInnerCircleColor(thumbColor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }

        if (backgroundDrawable == 0) {
            Log.e(TAG, "resource ids not right!");
            return;
        }
        ivBackground = new ImageView(context);
        ivBackground.setId(R.id.iv_cs_background);
        ivBackground.setImageResource(backgroundDrawable);
        ivBackground.setScaleType(ImageView.ScaleType.FIT_XY);
        this.setOnTouchListener(this);
        addView(ivBackground);

        addView(ccvThumb);
    }

    public void setBackgroundImage(int resId){
        ivBackground.setImageResource(resId);
    }

    private int HEIGHT_MULTIPLEXER = 0;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        Debug.d(TAG, "onLayout, l:" + l + ", t:" + t + ", r:" + r + ", b:" + b);
        int bgTop = getMeasuredHeight() / HEIGHT_MULTIPLEXER * (HEIGHT_MULTIPLEXER / 2);
        int bgBottom = bgTop + ivBackground.getMeasuredHeight();
        int height = getMeasuredHeight();
        ivBackground.layout(height / 2, bgTop, getMeasuredWidth() - height / 2, bgBottom);

        ccvThumb.layout(0, 0, ccvThumb.getMeasuredHeight(), ccvThumb.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int h = getMeasuredHeight();

        // Log.d(TAG, "H:" + h);

        int hSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);

        ccvThumb.measure(hSpec, hSpec);

        int backgroundHeight = MeasureSpec.makeMeasureSpec(h / HEIGHT_MULTIPLEXER, MeasureSpec.EXACTLY);

        int w = getMeasuredWidth();

        int wSpec = MeasureSpec.makeMeasureSpec(w - h, MeasureSpec.EXACTLY);

        ivBackground.measure(wSpec, backgroundHeight);
    }

    /**
     * 设置当前滑块位置
     * @param position 0 - 1 float
     */
    public void setPosition(float position, boolean genCallback) {
        if (position < 0 || position > 1) return;
        float x = (getWidth() - getHeight()) * position;
//        if (Math.abs(x - ccvThumb.getX()) > 0.2f * getWidth()) {
//            ObjectAnimator ox = ObjectAnimator.ofFloat(ccvThumb, "x", ccvThumb.getX(), x);
//            ox.setDuration((long) Math.abs(150 * (x - ccvThumb.getX()) / getWidth()));
//            ox.start();
//        }
        ccvThumb.setX(x);
        if (onPositionChangedListener != null && genCallback)
            onPositionChangedListener.onNewPosition(position, true);
    }

    public void setPosition(float position) {
        setPosition(position, false);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == this) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    getParent().requestDisallowInterceptTouchEvent(true);
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    // Log.d(TAG, "x:"+x + " thumb width:"+ivThumb.getWidth());
                    if (x > ccvThumb.getWidth() / 2 && x < getWidth() - ccvThumb.getWidth() / 2) {
                        // Log.d(TAG, "set x:"+(x-ivThumb.getWidth()/2));
                        ccvThumb.setX(x - ccvThumb.getWidth() / 2);
                        float position = (x - ccvThumb.getWidth() / 2) / (getWidth() - getHeight());
                        if (onPositionChangedListener != null) {
                            onPositionChangedListener.onNewPosition(position, event.getAction() == MotionEvent.ACTION_UP);
                        }

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            Object tag = ccvThumb.getTag();
                            if (tag != null && tag instanceof ObjectAnimator) {
                                ObjectAnimator prevOx = (ObjectAnimator) tag;
                                if (prevOx.isRunning()) {
                                    prevOx.cancel();
                                }
                            }
                            ObjectAnimator ox = ObjectAnimator.ofFloat(ccvThumb, "x", ccvThumb.getX(), x - ccvThumb.getWidth() / 2);
                            ox.setDuration((long) Math.abs(150 * (x - ccvThumb.getX() + ccvThumb.getWidth() / 2) / getWidth()));
                            ox.start();
                            ccvThumb.setTag(ox);
                        }

                    }

                    if (MotionEvent.ACTION_UP == event.getAction()) {
                        if (x <= ccvThumb.getWidth() / 2) {
                            if (onPositionChangedListener != null) {
                                onPositionChangedListener.onNewPosition(0, true);
                            }
                        }
                        if (x >= getWidth() - ccvThumb.getWidth() / 2) {
                            if (onPositionChangedListener != null) {
                                onPositionChangedListener.onNewPosition(1, true);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }
}
