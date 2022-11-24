package com.h.ruler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;

public class RuleView extends View {

    private static final String TAG = "RuleView";

    private OnScaleChangeListener onScaleChangeListener;

    //画布高度
    private float mHeight;
    //画布宽度
    private float mWidth;
    //刻度值颜色
    private int scaleTextColor;
    // 刻度数字的画笔
    private Paint textPaint;
    // 中心线画笔
    private Paint centerLinePaint;
    //小刻度画笔
    private Paint lowScalePaint;
    //中刻度画笔
    private Paint middleScalePaint;
    //高刻度画笔
    private Paint highScalePaint;
    //文字大小
    private int textSize = 30;
    //文字放大规格
    private int textZoomSize = 100;
    //刻度间距
    private float spacing = 100;
    //小刻度高度
    private float lowScaleHeight = 55;
    //中刻度高度
    private float middleScaleHeight = 75;
    //高刻度高度
    private float heightScaleHeight = 100;
    //开始刻度的值
    private int startValue = 3;
    //结束刻度的值
    private int endValue = 30;
    //是否显示中刻度线
    private boolean showMiddleScaleLine = true;
    //是否显示高刻度线
    private boolean showHeightScaleLine = true;
    //是否显示低刻度线
    private boolean showLowScaleLine = true;
    //低刻度颜色
    private int lowScaleLineColor;
    //中刻度颜色
    private int middleScaleLineColor;
    //高刻度颜色
    private int highScaleLineColor;
    //是否显示中刻度值
    private boolean showMiddleScaleNumber = true;
    //是否显示高刻度值
    private boolean showHeightScaleNumber = false;
    //是否显示低刻度值
    private boolean showLowScaleNumber = true;
    //当前刻度是否有放大效果
    private boolean zoom = true;
    //刻度线是否圆角
    private boolean isRoundScaleLine = false;
    //低刻度线宽度
    private int lowScaleStrokeWidth = 10;
    //中刻度线宽度
    private int middleScaleStrokeWidth = 10;
    //高刻度线宽度
    private int highScaleStrokeWidth = 10;
    //紧贴模式下数值和刻度之间的间隔
    private float valueScaleSpace = 40;
    //设置初始指向的刻度
    private int initValue = -1;
    //是否显示中心线
    private boolean showCenterLine = true;
    //中心线颜色
    private int centerLineColor;
    //中心线宽度
    private int centerLineStrokeWidth = 10;

    /**
     * 中间刻度单位间隔
     * 1~3~6 值为3
     * 1~5~10 值为5
     */
    private int middleSpaceValueSpace = 5;

    /**
     * 高刻度单位间隔
     * 1~3~6 值为3
     * 1~5~10 值为5
     */
    private int highSpaceValeSpace = 10;

    /**
     * 显示模式
     * 1、刻度值紧贴上面,刻度紧贴下面
     * 1      5       10
     * ！              ！
     * ！———————！——————！
     * <p>
     * 2、刻度紧贴上面，刻度值紧贴下面
     * ！------！-------！
     * ！               ！
     * 1       5       10
     * <p>
     * 3、刻度值在下和刻度紧贴居中
     * ！------！-------！
     * 1       5       10
     * <p>
     * 3、刻度值在上和刻度紧贴居中
     * 1       5       10
     * ！------！-------！
     */
    private int viewStyle = valueDownScaleUpStyle;

    public static final int valueDownScaleUpStyle = 1;

    public static final int valueUpScaleDownStyle = 2;

    public static final int valueDownScaleUpCloseStyle = 3;

    public static final int valueUpScaleDownCloseStyle = 4;

    private float leftXAxis = -1;

    private float rightXAxis = -1;

    //刻度值的文字高度
    private int textHeight;

    private boolean mIsExactly = false;

    public void setOnScaleChangeListener(OnScaleChangeListener onScaleChangeListener) {
        this.onScaleChangeListener = onScaleChangeListener;
    }

    public RuleView(Context context) {
        this(context, null);
    }

    public RuleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RuleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RuleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        initPaints();
        if (initValue == -1) {
            initValue = (endValue - startValue) / 2;
        }
        leftXAxis = -spacing * (initValue - startValue);
        rightXAxis = spacing * (endValue - initValue);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RuleView);
        initValue = typedArray.getInt(R.styleable.RuleView_initValue, -1);
        textSize = typedArray.getDimensionPixelSize(R.styleable.RuleView_scaleTextSize, 12);
        scaleTextColor = typedArray.getColor(R.styleable.RuleView_scaleTextColor, Color.GRAY);
        textZoomSize = typedArray.getDimensionPixelSize(R.styleable.RuleView_scaleTextZoomSize, 24);
        spacing = typedArray.getDimension(R.styleable.RuleView_scaleSpace, 50);
        lowScaleLineColor = typedArray.getColor(R.styleable.RuleView_lowScaleLineColor, Color.GRAY);
        middleScaleLineColor = typedArray.getColor(R.styleable.RuleView_middleScaleLineColor, Color.GRAY);
        highScaleLineColor = typedArray.getColor(R.styleable.RuleView_highScaleLineColor, Color.GRAY);
        lowScaleHeight = typedArray.getDimension(R.styleable.RuleView_lowScaleHeight, 20);
        middleScaleHeight = typedArray.getDimension(R.styleable.RuleView_middleScaleHeight, 40);
        heightScaleHeight = typedArray.getDimension(R.styleable.RuleView_heightScaleHeight, 80);
        startValue = typedArray.getInt(R.styleable.RuleView_scaleStartValue, 3);
        endValue = typedArray.getInt(R.styleable.RuleView_scaleEndValue, 30);
        showLowScaleLine = typedArray.getBoolean(R.styleable.RuleView_showLowScaleLine, true);
        showMiddleScaleLine = typedArray.getBoolean(R.styleable.RuleView_showMiddleScaleLine, true);
        showHeightScaleLine = typedArray.getBoolean(R.styleable.RuleView_showHeightScaleLine, true);
        showLowScaleNumber = typedArray.getBoolean(R.styleable.RuleView_showLowScaleNumber, true);
        showMiddleScaleNumber = typedArray.getBoolean(R.styleable.RuleView_showMiddleScaleNumber, true);
        showHeightScaleNumber = typedArray.getBoolean(R.styleable.RuleView_showHeightScaleNumber, true);
        zoom = typedArray.getBoolean(R.styleable.RuleView_zoom, true);
        isRoundScaleLine = typedArray.getBoolean(R.styleable.RuleView_roundScaleLine, true);
        lowScaleStrokeWidth = (int) typedArray.getDimension(R.styleable.RuleView_lowScaleStrokeWidth, 5);
        middleScaleStrokeWidth = (int) typedArray.getDimension(R.styleable.RuleView_middleScaleStrokeWidth, 5);
        highScaleStrokeWidth = (int) typedArray.getDimension(R.styleable.RuleView_highScaleStrokeWidth, 5);
        valueScaleSpace = typedArray.getDimension(R.styleable.RuleView_valueScaleSpace, 20);
        middleSpaceValueSpace = typedArray.getInt(R.styleable.RuleView_middleSpaceValueSpace, 5);
        highSpaceValeSpace = typedArray.getInt(R.styleable.RuleView_highSpaceValeSpace, 10);
        viewStyle = typedArray.getInt(R.styleable.RuleView_viewStyle, 3);
        centerLineColor = typedArray.getColor(R.styleable.RuleView_centerLineColor, Color.GRAY);
        centerLineStrokeWidth = (int) typedArray.getDimension(R.styleable.RuleView_centerLineStrokeWidth, 5);
        showCenterLine = typedArray.getBoolean(R.styleable.RuleView_showCenterLine, true);
        typedArray.recycle();
    }

    private void initPaints() {
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(scaleTextColor);
        textPaint.setTextSize(textSize);
        textPaint.setStrokeWidth(3);
        Rect rect = new Rect();
        if (zoom) {
            textPaint.setTextSize(textZoomSize);
            textPaint.getTextBounds(String.valueOf(startValue), 0, String.valueOf(startValue).length(), rect);
        } else {
            textPaint.getTextBounds(String.valueOf(startValue), 0, String.valueOf(startValue).length(), rect);
        }
        textHeight = rect.bottom - rect.top;

        lowScalePaint = new Paint();
        lowScalePaint.setStyle(Paint.Style.FILL);
        lowScalePaint.setColor(lowScaleLineColor);
        lowScalePaint.setStrokeWidth(lowScaleStrokeWidth);
        if (isRoundScaleLine) {
            lowScalePaint.setStrokeCap(Paint.Cap.ROUND);
        }

        middleScalePaint = new Paint();
        middleScalePaint.setStyle(Paint.Style.FILL);
        middleScalePaint.setColor(middleScaleLineColor);
        middleScalePaint.setStrokeWidth(middleScaleStrokeWidth);
        if (isRoundScaleLine) {
            middleScalePaint.setStrokeCap(Paint.Cap.ROUND);
        }

        highScalePaint = new Paint();
        highScalePaint.setStyle(Paint.Style.FILL);
        highScalePaint.setColor(highScaleLineColor);
        highScalePaint.setStrokeWidth(highScaleStrokeWidth);
        if (isRoundScaleLine) {
            highScalePaint.setStrokeCap(Paint.Cap.ROUND);
        }

        centerLinePaint = new Paint();
        centerLinePaint.setStyle(Paint.Style.FILL);
        centerLinePaint.setColor(centerLineColor);
        centerLinePaint.setStrokeWidth(centerLineStrokeWidth);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = 0;
        if (heightModel == MeasureSpec.AT_MOST) {
            mIsExactly = false;
            if (viewStyle == valueDownScaleUpCloseStyle || viewStyle == valueUpScaleDownCloseStyle) {
                heightSize = (int) (textHeight + (showHeightScaleLine ? heightScaleHeight : showMiddleScaleLine ? middleScaleHeight : lowScaleHeight) + valueScaleSpace + getPaddingTop() + getPaddingBottom());
            } else if (viewStyle == valueDownScaleUpStyle || viewStyle == valueUpScaleDownStyle) {
                heightSize = MeasureSpec.getSize(heightMeasureSpec) / 5 + getPaddingTop() + getPaddingBottom();
            }
        } else if (heightModel == MeasureSpec.EXACTLY) {
            heightSize = MeasureSpec.getSize(heightMeasureSpec) + getPaddingTop() + getPaddingBottom();
            mIsExactly = true;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    float onTouchLocationX = -1;

    float offsetX = 0;

    float lastMoveX = 0;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchLocationX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = event.getX() - onTouchLocationX + lastMoveX;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                lastMoveX = offsetX;
                invalidate();
                startAnimation();
                break;
        }
        return true;
    }

    private float calculateOffset() {
        float v = offsetX % spacing;
        Log.d(TAG, "calculateOffset: " + v);
        return v;
    }

    private void startAnimation() {
        float offset;
        if (offsetX > 0 && offsetX > Math.abs(leftXAxis)) {
            //超出左边界的偏移
            offset = offsetX - Math.abs(leftXAxis);
        } else if (offsetX < 0 && offsetX < -rightXAxis) {
            //超出右边界的偏移
            offset = (Math.abs(rightXAxis) - Math.abs(offsetX));
        } else {
            //刻度尺内的偏移
            offset = calculateOffset();
        }
        ValueAnimator animator = ValueAnimator.ofFloat(0, offset);
        animator.setDuration(200);
        animator.addUpdateListener(valueAnimator -> {
            Float animatedValue = (Float) valueAnimator.getAnimatedValue();
            offsetX = lastMoveX - animatedValue;
            RuleView.this.invalidate();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                super.onAnimationEnd(animation, isReverse);
                lastMoveX = offsetX;
            }
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScale(canvas);
        drawScaleNumber(canvas);
    }

    //绘制刻度值从中间开始绘制，向两边依次绘制
    private void drawScaleNumber(Canvas canvas) {
        int currentValue;
        canvas.save();
        if (viewStyle == valueDownScaleUpStyle || viewStyle == valueUpScaleDownStyle) {
            canvas.translate(mWidth / 2, 0);
        } else if ((viewStyle == valueUpScaleDownCloseStyle || viewStyle == valueDownScaleUpCloseStyle) && mIsExactly) {
            canvas.translate(mWidth / 2, mHeight / 2);
        } else if (viewStyle == valueUpScaleDownCloseStyle) {
            canvas.translate(mWidth / 2, textHeight + valueScaleSpace / 2);
        } else if (viewStyle == valueDownScaleUpCloseStyle) {
            canvas.translate(mWidth / 2, (showHeightScaleLine ? heightScaleHeight : showMiddleScaleLine ? middleScaleHeight : lowScaleHeight) + (valueScaleSpace / 2));
        }
        // 向绘制左边 <----
        float currentValueXAxis = 0 + offsetX;
        currentValue = initValue;
        while (currentValueXAxis >= -(mWidth / 2) && currentValue >= startValue) {
            drawScaleNumberItem(canvas, currentValue, (int) currentValueXAxis);
            currentValueXAxis = (int) (currentValueXAxis - spacing);
            currentValue = currentValue - 1;
        }
        //向左绘制 ----->
        currentValueXAxis = 0 + offsetX;
        currentValue = initValue;
        while (currentValueXAxis <= mWidth && currentValue <= endValue) {
            drawScaleNumberItem(canvas, currentValue, (int) currentValueXAxis);
            currentValueXAxis = (int) (currentValueXAxis + spacing);
            currentValue = currentValue + 1;
        }
        canvas.restore();
    }

    //绘制刻度线从中间开始绘制，向两边依次绘制
    private void drawScale(Canvas canvas) {
        int currentValue;
        canvas.save();
        if (viewStyle == valueDownScaleUpStyle || viewStyle == valueUpScaleDownStyle) {
            canvas.translate(mWidth / 2, 0);
        } else if ((viewStyle == valueUpScaleDownCloseStyle || viewStyle == valueDownScaleUpCloseStyle) && mIsExactly) {
            canvas.translate(mWidth / 2, mHeight / 2);
        } else if (viewStyle == valueUpScaleDownCloseStyle) {
            canvas.translate(mWidth / 2, textHeight + valueScaleSpace / 2);
        } else if (viewStyle == valueDownScaleUpCloseStyle) {
            canvas.translate(mWidth / 2, (showHeightScaleLine ? heightScaleHeight : showMiddleScaleLine ? middleScaleHeight : lowScaleHeight) + (valueScaleSpace / 2));
        }
        // 向绘制左边 <----
        float currentValueXAxis = 0 + offsetX;
        currentValue = initValue;
        //最左边的坐标 计算边界使用。
        while (currentValueXAxis >= -(mWidth / 2) && currentValue >= startValue) {
            drawScaleLineItem(currentValueXAxis, currentValue, canvas);
            currentValueXAxis = (int) (currentValueXAxis - spacing);
            currentValue = currentValue - 1;
        }
        Log.d(TAG, "drawScale: " + leftXAxis);
        //向左绘制 ----->
        currentValueXAxis = 0 + offsetX;
        currentValue = initValue;
        while (currentValueXAxis <= mWidth && currentValue <= endValue) {
            drawScaleLineItem(currentValueXAxis, currentValue, canvas);
            currentValueXAxis = (int) (currentValueXAxis + spacing);
            currentValue = currentValue + 1;
        }
        Log.d(TAG, "drawScale: " + rightXAxis);
        canvas.restore();
    }

    //绘制刻度值
    private void drawScaleNumberItem(Canvas canvas, int currentValue, int currentValueXAxis) {
        if (currentValueXAxis == 0 || Math.abs(currentValueXAxis) < spacing * 0.3) {
            if (onScaleChangeListener != null && !String.valueOf(currentValue).equals(callbackCache)) {
                callbackCache = String.valueOf(currentValue);
                Log.d(TAG, "onScaleChangeListener: text :" + currentValue);
                onScaleChangeListener.onScaleChange(currentValue);
            }
        }
        if ((currentValue % middleSpaceValueSpace == 0 && showHeightScaleNumber)
                || (currentValue % highSpaceValeSpace == 0 && showMiddleScaleNumber)
                || showLowScaleNumber
        ) {
            drawText(canvas, String.valueOf(currentValue), currentValueXAxis, 0);
        }
    }

    //绘制刻度线
    private void drawScaleLineItem(float currentValueXAxis, int currentValue, Canvas canvas) {
        float startCenterX = 0,endCenterX = 0,startCenterY =0,endCenterY=0;
        float startLowX=0, endLowX=0, startLowY=0, endLowY=0;
        float startMiddleX=0, endMiddleX=0, startMiddleY=0, endMiddleY=0;
        float startHighX=0, endHighX=0, startHighY=0, endHighY=0;
        if (viewStyle == valueDownScaleUpStyle) {
            startCenterX = -mWidth;
            startCenterY = 0;
            endCenterX = mWidth;
            endCenterY = 0;
            startLowX = currentValueXAxis;
            startLowY = 0;
            endLowX = currentValueXAxis;
            endLowY = lowScaleHeight;
            startMiddleX = currentValueXAxis;
            startMiddleY = 0;
            endMiddleX = currentValueXAxis;
            endMiddleY = middleScaleHeight;
            startHighX = currentValueXAxis;
            startHighY = 0;
            endHighX = currentValueXAxis;
            endHighY = heightScaleHeight;
        } else if (viewStyle == valueDownScaleUpCloseStyle) {
            startCenterX = -mWidth;
            startCenterY = -valueScaleSpace / 2;
            endCenterX = mWidth;
            endCenterY = -valueScaleSpace / 2;
            startLowX = currentValueXAxis;
            startLowY = -lowScaleHeight - valueScaleSpace / 2;
            endLowX = currentValueXAxis;
            endLowY = -valueScaleSpace / 2;
            startMiddleX = currentValueXAxis;
            startMiddleY = -middleScaleHeight - valueScaleSpace / 2;
            endMiddleX = currentValueXAxis;
            endMiddleY = -valueScaleSpace / 2;
            startHighX = currentValueXAxis;
            startHighY = -heightScaleHeight - valueScaleSpace / 2;
            endHighX = currentValueXAxis;
            endHighY = -valueScaleSpace / 2;
        } else if (viewStyle == valueUpScaleDownStyle) {
            startCenterX = -mWidth;
            startCenterY = mHeight;
            endCenterX = mWidth;
            endCenterY = mHeight;
            startLowX = currentValueXAxis;
            startLowY = mHeight;
            endLowX = currentValueXAxis;
            endLowY = mHeight - lowScaleHeight;
            startMiddleX = currentValueXAxis;
            startMiddleY = mHeight;
            endMiddleX = currentValueXAxis;
            endMiddleY = mHeight - middleScaleHeight;
            startHighX = currentValueXAxis;
            startHighY = mHeight;
            endHighX = currentValueXAxis;
            endHighY = mHeight - heightScaleHeight;
        } else if (viewStyle == valueUpScaleDownCloseStyle) {
            startCenterX = -mWidth;
            startCenterY = valueScaleSpace / 2;
            endCenterX = mWidth;
            endCenterY = valueScaleSpace / 2;
            startLowX = currentValueXAxis;
            startLowY = 0 + valueScaleSpace / 2;
            endLowX = currentValueXAxis;
            endLowY = lowScaleHeight + valueScaleSpace / 2;
            startMiddleX = currentValueXAxis;
            startMiddleY = 0 + valueScaleSpace / 2;
            endMiddleX = currentValueXAxis;
            endMiddleY = middleScaleHeight + valueScaleSpace / 2;
            startHighX = currentValueXAxis;
            startHighY = 0 + valueScaleSpace / 2;
            endHighX = currentValueXAxis;
            endHighY = heightScaleHeight + valueScaleSpace / 2;
        }
        if (showCenterLine) {
            canvas.drawLine(startCenterX, startCenterY, endCenterX, endCenterY, centerLinePaint);
        }
        if (showLowScaleLine) {
            canvas.drawLine(startLowX, startLowY, endLowX, endLowY, lowScalePaint);
        }
        if (showMiddleScaleLine) {
            if (currentValue % middleSpaceValueSpace == 0) {
                canvas.drawLine(startMiddleX, startMiddleY, endMiddleX, endMiddleY, middleScalePaint);
            }
        }
        if (showHeightScaleLine) {
            if (currentValue % highSpaceValeSpace == 0) {
                canvas.drawLine(startHighX, startHighY, endHighX, endHighY, highScalePaint);
            }
        }

    }

    //记录上一次回调的值，如果一样就不回调了
    private String callbackCache = String.valueOf(initValue);

    private void drawText(Canvas canvas, String text, int x, int y) {
        Rect rect = new Rect();
        Log.d(TAG, "drawText: x :" + x);
        Log.d(TAG, "drawText: text :" + text);

        float size;
        if (zoom && Math.abs(x) <= spacing) {
            //快速绘制时可能会跨个某些值不会只设置区间为0.6个单位可以比较好的解决没有回调的问题
            if (x == 0) {
                size = textZoomSize;
            } else {
                size = (((spacing - Math.abs(x)) / spacing) * (textZoomSize - textSize)) + textSize;
            }
        } else {
            size = textSize;
        }
        textPaint.setTextSize(size);
        textPaint.getTextBounds(text, 0, text.length(), rect);
        if (viewStyle == valueDownScaleUpStyle) {
            canvas.drawText(text, 0, text.length(), (float) (x + ((rect.left * 1.0 - rect.right * 1.0) / 2)), mHeight, textPaint);
        } else if (viewStyle == valueDownScaleUpCloseStyle) {
            canvas.drawText(text, 0, text.length(), (float) (x + ((rect.left * 1.0 - rect.right * 1.0) / 2)), y + (rect.bottom - rect.top) + valueScaleSpace / 2, textPaint);
        } else if (viewStyle == valueUpScaleDownStyle) {
            canvas.drawText(text, 0, text.length(), (float) (x + ((rect.left * 1.0 - rect.right * 1.0) / 2)), y + (rect.bottom - rect.top), textPaint);
        } else if (viewStyle == valueUpScaleDownCloseStyle) {
            canvas.drawText(text, 0, text.length(), (float) (x + ((rect.left * 1.0 - rect.right * 1.0) / 2)), y - valueScaleSpace / 2, textPaint);

        }
    }

    public interface OnScaleChangeListener {
        void onScaleChange(int value);
    }
}
