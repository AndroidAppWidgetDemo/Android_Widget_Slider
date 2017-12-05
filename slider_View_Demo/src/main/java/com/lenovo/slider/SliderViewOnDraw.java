package com.lenovo.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * 滑块
 *
 * @author xiaxl1
 */
public class SliderViewOnDraw extends View {

    private static final String TAG = SliderViewOnDraw.class.getSimpleName();

    // 移动时，没有动画(直接一帧跳过去)
    public static final int SHOW_NO_ANIMATION = 2;
    // 移动、缩放动画
    public static final int SHOW_TRANSLATE_AND_SCALE_ANIMATION = 0;

    /**
     * 无论什么样的距离7帧走完
     */
    private final int duration = 7;
    // 走到第几帧了
    private int count = 0;

    //
    private DrawArg drawArg;

    // 滑块 画笔
    private Paint sliderPaint;

    /**
     * 当前的位置
     */
    private int sliderViewLeft = 0;
    private int sliderViewRight = 0;

    /**
     *
     */
    private OnMoveListener listener;

    /**
     * 父View的Location
     */
    private int[] parentLocation;

    /**
     * 构造方法
     *
     * @param paramContext
     */
    public SliderViewOnDraw(Context paramContext) {
        super(paramContext);
        init(paramContext);
    }

    public SliderViewOnDraw(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext);
    }

    public SliderViewOnDraw(Context paramContext,
                            AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext);
    }

    /**
     * 初始化
     *
     * @param paramContext
     */
    private void init(Context paramContext) {
        // 设置背景颜色为透明
        setBackgroundColor(0);
        // 滑块颜色
        resetColor(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG, "----onDraw-----");

        // 帧数加1
        this.count++;

        /**
         *
         */
        if ((this.count <= this.duration) && (this.drawArg != null)) {
            // 原始Left位置+水平位移
            this.sliderViewLeft = (this.drawArg.originLeft + getSinRealTimeLength(
                    this.count, this.drawArg.horizontalDistance));
            this.sliderViewRight = (this.sliderViewLeft
                    + getSinRealTimeLength(this.count, this.drawArg.widthDelta) + this.drawArg.originWidth);

            Log.d(TAG, "drawViewLeft: " + sliderViewLeft);
            Log.d(TAG, "drawViewRight: " + sliderViewRight);

            // 绘制下一帧
            invalidate();
        }

        if (this.getVisibility() == View.VISIBLE) {
            // 绘制
            canvas.drawRect(this.sliderViewLeft, 0, this.sliderViewRight,
                    this.getHeight(), sliderPaint);
        }

        //
        if ((this.listener != null) && (this.count == this.duration)) {
            this.listener.endMove();
        }

    }

    /**
     * 移动到目标View
     *
     * @param targetViewLocation 目标View左上角坐标
     * @param targetViewWH       目标View的宽高
     * @param paramInt
     */
    private void MoveViewToPosition(int[] targetViewLocation,
                                    int[] targetViewWH, int annimationType) {
        Log.d(TAG, "----MoveViewToPosition-----");

        if (this.listener != null) {
            this.listener.startMove();
        }

        switch (annimationType) {
            default:
            case SHOW_NO_ANIMATION:
                // // 移动时，没有有动画(直接一帧跳过去)
                this.sliderViewLeft = targetViewLocation[0];
                //
                this.sliderViewRight = (this.sliderViewLeft + targetViewWH[0]);
                this.drawArg = null;
                //
                break;

            case SHOW_TRANSLATE_AND_SCALE_ANIMATION:
                // 重置count
                this.count = 0;
                //
                this.drawArg = new DrawArg();
                /**
                 * 水平。目标位置-当前View的位置
                 */
                this.drawArg.horizontalDistance = (targetViewLocation[0] - this.sliderViewLeft);
                /**
                 * 目标宽高 - 当前宽高
                 */
                this.drawArg.widthDelta = (targetViewWH[0] - (this.sliderViewRight - this.sliderViewLeft));
                //
                this.drawArg.originLeft = this.sliderViewLeft;
                this.drawArg.originWidth = (this.sliderViewRight - this.sliderViewLeft);
                //
                break;
        }
        // 刷新界面
        invalidate();
    }

    /**
     * 正弦函数进行数值变化
     *
     * @param count    第几帧
     * @param distance 要移动的距离
     * @return
     */
    private int getSinRealTimeLength(int count, int distance) {
        return (int) (Math
                .sin(3.141592653589793D / (2 * this.duration) * count) * distance);
    }

    /**
     * 获取相对父布局的位置
     *
     * @param destinViewLocation
     */
    private void getRelativePositionInParent(int[] destinViewLocation) {
        Log.e(TAG, "---getRelativePositionInParent---");

        initParentLocation();
        destinViewLocation[0] -= this.parentLocation[0];
        destinViewLocation[1] -= this.parentLocation[1];
    }

    private void initParentLocation() {
        this.parentLocation = new int[2];
        if ((getParent() == null) || !(getParent() instanceof ViewGroup)) {
            return;
        }
        ((View) getParent()).getLocationOnScreen(this.parentLocation);
    }

    /**
     * 移动到目标View
     *
     * @param destinView    目标View
     * @param animationType 动画类型
     */
    public void moveToDestin(final View destinView, final int animationType) {

        Log.d(TAG, "-----moveToDestin(View destinView, int animationType)-----");

        if (destinView == null) {
            Log.e(TAG, "destinView == null");
            return;
        }
        if (destinView.getWidth() == 0) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                    //
                    int[] destinViewLocation = new int[2];
                    destinView.getLocationOnScreen(destinViewLocation);
                    //
                    int[] destinViewWH = new int[2];
                    destinViewWH[0] = destinView.getWidth();
                    destinViewWH[1] = destinView.getHeight();
                    //
                    moveToDestin(destinViewLocation, destinViewWH, animationType);

                }
            });

        } else {
            //
            int[] destinViewLocation = new int[2];
            destinView.getLocationOnScreen(destinViewLocation);
            //
            int[] destinViewWH = new int[2];
            destinViewWH[0] = destinView.getWidth();
            destinViewWH[1] = destinView.getHeight();
            //
            moveToDestin(destinViewLocation, destinViewWH, animationType);
        }


    }

    /**
     * 移动到目标View
     *
     * @param destinView    目标类型
     * @param animationType 动画类型
     * @param drawableId    图片id
     * @param whiteLength   留白大小
     */
    public void moveToDestin(View destinView, int animationType, int colorId) {
        // 滑块颜色
        resetColor(colorId);
        //
        moveToDestin(destinView, animationType);
    }

    /**
     * @param destinViewLocation 目标View的宽高
     * @param destinViewWH2      目标View的宽高
     * @param animationType      动画类型
     */
    private void moveToDestin(int[] destinViewLocation, int[] destinViewWH,
                              int animationType) {
        Log.e(TAG,
                "moveToDestin(int[] destinViewLocation, int[] destinViewWH,int animationType)");

        // 当前View的宽高值
        if (destinViewWH == null) {
            destinViewWH = new int[2];
            destinViewWH[0] = (this.sliderViewRight - this.sliderViewLeft);
            destinViewWH[1] = (this.getHeight() - 0);
        }

        /**
         * 获取相对父布局的位置
         */
        getRelativePositionInParent(destinViewLocation);

        /**
         * 开始移动
         */
        MoveViewToPosition(destinViewLocation, destinViewWH, animationType);

    }

    private void resetColor(int colorId) {
        /**
         * 初始化颜色信息
         */
        Log.d(TAG, "----resetColor----");

        if (colorId == 0) {
            colorId = android.R.color.holo_red_light;
        }

        sliderPaint = new Paint();
        sliderPaint.setColor(this.getResources().getColor(colorId));

    }

    /**
     * @author xiaxl1
     */
    private static class DrawArg {
        //

        int originLeft;

        int originWidth;
        //

        int widthDelta;
        //
        int horizontalDistance;
    }

    /**
     * 开启移动，结束移动的Listener
     *
     * @author xiaxl1
     */
    public static abstract interface OnMoveListener {
        public abstract void endMove();

        public abstract void startMove();
    }

    /**
     *
     */
    public void setOnMoveListener(OnMoveListener paramOnMoveListener) {
        this.listener = paramOnMoveListener;
    }

}