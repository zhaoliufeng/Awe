package com.ws.mesh.awe.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lh on 2015/11/3.
 */
public class ColorCircleView extends View {

    private int outerCircleColor = 0xffcccccc;
    private int outerCircleWidth = 1;

    private float innerCircleRatio = 0.7f;

    private int innerCircleColor = Color.RED;

    private Paint paint;

    public ColorCircleView(Context context) {
        super(context);
        init();
    }

    public ColorCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        paint = new Paint();
    }

    private RectF outerRect = new RectF();
    private RectF innerRect = new RectF();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();

        outerRect.set(outerCircleWidth, outerCircleWidth, w - outerCircleWidth, h - outerCircleWidth);

        paint.setAntiAlias(true);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawArc(outerRect, 0, 360, false, paint);

        paint.setColor(outerCircleColor);
        paint.setStrokeWidth(outerCircleWidth);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawArc(outerRect, 0, 360, false, paint);
        paint.setColor(innerCircleColor);
        paint.setStyle(Paint.Style.FILL);

        innerRect.set(w / 2 - innerCircleRatio * w / 2, h / 2 - innerCircleRatio * h / 2, w / 2 + innerCircleRatio * w / 2, h / 2 + innerCircleRatio * h / 2);
        canvas.drawArc(innerRect, 0, 360, false, paint);
    }

    public void setInnerCircleColor(int color){
        this.innerCircleColor = color;
        invalidate();
    }

}
