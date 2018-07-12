package com.ws.mesh.awe.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.utils.MathUtil;
import com.ws.mesh.awe.utils.ValueWrappers;


/**
 * Created by zxd on 2016/09/28.
 */
public class ColorPickView extends View {
    private Context context;
    private int bigCircle; // 外圈半径
    private int rudeRadius; // 可移动小球的半径
    private int centerColor; // 可移动小球的颜色
    private int rudeStrokeWidth = 5;
    private Bitmap bitmapBack; // 背景图片
    private Paint mPaint; // 背景画笔
    private Paint mCenterPaint; // 可移动小球画笔
    private Point centerPoint;// 中心位置
    private Point mRockPosition;// 小球当前位置
    private OnColorChangedListener listener; // 小球移动的监听
    private int length; // 小球到中心位置的距离
    private double angel = 0.0 /* 0 - 360.0 */,
            linePercent = 1.0 /* 0 - 1.0 */,
            radio = 1.0 /* 0 - 1.0 */;
    private float[] mHSB = new float[3];

    public ColorPickView(Context context) {
        super(context);
    }

    public ColorPickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    public ColorPickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.listener = listener;
    }

    /**
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        // 获取自定义组件的属性
        TypedArray types = context.obtainStyledAttributes(attrs,
                R.styleable.color_picker);
        try {
            bigCircle = types.getDimensionPixelOffset(
                    R.styleable.color_picker_circle_radius, 115);
            rudeRadius = types.getDimensionPixelOffset(
                    R.styleable.color_picker_center_radius, 10);
            centerColor = types.getColor(R.styleable.color_picker_center_color,
                    Color.WHITE);
        } finally {
            types.recycle(); // TypeArray用完需要recycle
        }
        // 将背景图片大小设置为属性设置的直径
        bitmapBack = BitmapFactory.decodeResource(getResources(),
                R.drawable.hsb_circle_hard);
        bitmapBack = Bitmap.createScaledBitmap(bitmapBack, bigCircle * 2,
                bigCircle * 2, false);
        // 中心位置坐标
        centerPoint = new Point(bigCircle, bigCircle);
        mRockPosition = new Point(centerPoint);
        // 初始化背景画笔和可移动小球的画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mCenterPaint = new Paint();
        mCenterPaint.setAntiAlias(true);
        mCenterPaint.setColor(centerColor);
        mCenterPaint.setStrokeWidth(rudeStrokeWidth);
        mCenterPaint.setStyle(Paint.Style.STROKE);
    }

    public void setBackgroundImg(boolean isColor) {
        if (isColor) {
            bitmapBack = BitmapFactory.decodeResource(getResources(),
                    R.drawable.hsb_circle_hard);
            mCenterPaint.setColor(0xFFFFFFFF);
        } else {
            bitmapBack = BitmapFactory.decodeResource(getResources(),
                    R.drawable.icon_ctrl_warm_light_mode);
            mCenterPaint.setColor(0xFFC1C1C1);
        }
        centerPoint = new Point(bigCircle, bigCircle);

        bitmapBack = Bitmap.createScaledBitmap(bitmapBack, bigCircle * 2,
                bigCircle * 2, false);
        invalidate();
    }

    public void setCWBgImg(boolean isCold) {
        if (isCold) {
            bitmapBack = BitmapFactory.decodeResource(getResources(),
                    R.drawable.icon_ctrl_c_mode);
            mCenterPaint.setColor(0xFFC1C1C1);
        } else {
            bitmapBack = BitmapFactory.decodeResource(getResources(),
                    R.drawable.icon_ctrl_w_mode);
            mCenterPaint.setColor(0xFFFFFFFF);
        }
        centerPoint = new Point(bigCircle, bigCircle);

        bitmapBack = Bitmap.createScaledBitmap(bitmapBack, bigCircle * 2,
                bigCircle * 2, false);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画背景图片
        canvas.drawBitmap(bitmapBack, 0, 0, mPaint);
        // 画中心小球
        canvas.drawCircle(mRockPosition.x, mRockPosition.y, rudeRadius,
                mCenterPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final ValueWrappers.Bool reqUpdate = new ValueWrappers.Bool(false);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下
                getParent().requestDisallowInterceptTouchEvent(true);
            case MotionEvent.ACTION_UP:// 抬起
            case MotionEvent.ACTION_MOVE: // 移动
                reqUpdate.value = true;
                length = getLength(event.getX(), event.getY(), centerPoint.x,
                        centerPoint.y);
                if (length <= bigCircle - rudeRadius) {
                    mRockPosition.set((int) event.getX(), (int) event.getY());
                } else {
                    mRockPosition = getBorderPoint(centerPoint, new Point(
                            (int) event.getX(), (int) event.getY()), (bigCircle - rudeRadius - 5));
                }

                double cX = centerPoint.x,
                        cY = centerPoint.y,
                        pX = event.getX(),
                        pY = event.getY();

                angel = MathUtil.Angel(pX - cX, pY - cY) + 90; // [-180, 180] => [0, 360]
                if (angel < 0)
                    angel += 360.0;

                double deltaX = Math.abs(cX - pX), deltaY = (cY - pY);
                radio = Math.pow(deltaX * deltaX + deltaY * deltaY, 0.5) / bigCircle  ;
                if (radio <= 0) radio = 0;
                if (radio >= 1.0) radio = 1.0;
                break;
            default:
                break;
        }

        final double hue = angel / 360.0,
                sat = radio,
                brt = linePercent;
//		Util.UIColor c = new Util.UIColor(hue, sat, brt);
        mHSB[0] = (float) hue;
        mHSB[1] = (float) sat;
        mHSB[2] = (float) brt;

        if (listener != null)
            listener.onColorChange(mHSB, reqUpdate.value);
        invalidate(); // 更新画布
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 视图大小设置为直径
        setMeasuredDimension(bigCircle * 2, bigCircle * 2);
    }

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static int getLength(float x1, float y1, float x2, float y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * @param a
     * @param b
     * @param cutRadius
     * @return
     */
    public static Point getBorderPoint(Point a, Point b, int cutRadius) {
        float radian = getRadian(a, b);
        return new Point(a.x + (int) (cutRadius * Math.cos(radian)), a.x
                + (int) (cutRadius * Math.sin(radian)));
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static float getRadian(Point a, Point b) {
        float lenA = b.x - a.x;
        float lenB = b.y - a.y;
        float lenC = (float) Math.sqrt(lenA * lenA + lenB * lenB);
        float ang = (float) Math.acos(lenA / lenC);
        ang = ang * (b.y < a.y ? -1 : 1);
        return ang;
    }

    public void setPoint(float[] hsb) {
        Log.i("color", "h == " + hsb[0] + ",,s==" + hsb[1] + ",,b==" + hsb[2]);
        float angle = hsb[0];
        float currLength = hsb[1] * bigCircle;
        float y = 0;
        float x = 0;
        if (0 <= angle && angle <= 90) {
            y = (float) (centerPoint.y - Math.cos(angle * Math.PI / 180) * currLength);
            x = (float) (Math.sin(angle * Math.PI / 180) * currLength + centerPoint.x);
        } else if (angle > 90 && angle <= 180) {
            x = (float) (Math.cos((angle - 90) * Math.PI / 180) * currLength + centerPoint.x);
            y = (float) (Math.sin((angle - 90) * Math.PI / 180) * currLength + centerPoint.y);
        } else if (angle > 180 && angle <= 270) {
            x = (float) (-Math.sin((angle - 180) * Math.PI / 180) * currLength + centerPoint.x);
            y = (float) (Math.cos((angle - 180) * Math.PI / 180) * currLength + centerPoint.y);
        } else {
            y = (float) (-Math.sin((angle - 270) * Math.PI / 180) * currLength + centerPoint.y);
            x = (float) (-Math.cos((angle - 270) * Math.PI / 180) * currLength + centerPoint.x);
        }

        length = getLength(x, y, centerPoint.x,
                centerPoint.y);
        if (length <= bigCircle - rudeRadius) {
            mRockPosition.set((int) x, (int) y);
        } else {
            mRockPosition = getBorderPoint(centerPoint, new Point(
                    (int) x, (int) y), bigCircle - rudeRadius - 5);
        }

        angel = MathUtil.Angel(x - centerPoint.x, y - centerPoint.y) + 90; // [-180, 180] => [0, 360]
        if (angel < 0)
            angel += 360.0;

        double deltaX = Math.abs(centerPoint.x - x), deltaY = (centerPoint.y - y);
        radio = Math.pow(deltaX * deltaX + deltaY * deltaY, 0.5) / bigCircle * 2.0;
        if (radio <= 0) radio = 0;
        if (radio >= 1.0) radio = 1.0;

        final double hue = angel / 360.0,
                sat = radio,
                brt = linePercent;
//		Util.UIColor c = new Util.UIColor(hue, sat, brt);
        mHSB[0] = (float) hue;
        mHSB[1] = (float) sat;
        mHSB[2] = (float) brt;

//		listener.onColorChange(mHSB, true);
        invalidate();

    }

    // 颜色发生变化的回调接口
    public interface OnColorChangedListener {
        void onColorChange(float[] hsb, boolean reqUpdate);
    }
}
