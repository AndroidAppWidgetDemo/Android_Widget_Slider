package com.letv.jjfa_focus;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.android_test.R;

public class FocusViewOnDraw extends View {

	private static final String TAG = FocusViewOnDraw.class.getSimpleName();

	// 移动焦点框时，没有动画(直接一帧跳过去)
	public static final int SHOW_NO_ANIMATION = 2;
	// 位置不变，放大缩小动画
	public static final int SHOW_SCALE_ANIMATION = 1;
	// 移动、缩放动画
	public static final int SHOW_TRANSLATE_AND_SCALE_ANIMATION = 0;

	/**
	 * 留白大小
	 */
	private int bitMapWhiteLength = 26;

	/**
	 * 无论什么样的距离7帧走完
	 */
	private final int duration = 7;
	// 走到第几帧了
	private int count = 0;
	// 留白大小调节值
	private int whiteLengthOffect;

	//
	private DrawArg drawArg;

	/**
	 * 当前焦点框的位置
	 */
	private int drawViewBottom = 0;
	private int drawViewLeft = 0;
	private int drawViewRight = 0;
	private int drawViewTop = 0;

	/**
	 *
	 */
	private OnMoveListener listener;

	/**
	 * 父View的Location
	 */
	private int[] parentLocation;

	/**
	 * bitMap的原始宽高
	 */
	private int bitmapHeight;
	private int bitmapWidth;

	/**
	 *
	 */
	private NinePatch ninePatch;
	private Map<Integer, NinePatch> ninePatchMap;

	/**
	 * 构造方法
	 *
	 * @param paramContext
	 */
	public FocusViewOnDraw(Context paramContext) {
		super(paramContext);
		init(paramContext);
	}

	public FocusViewOnDraw(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext);
	}

	public FocusViewOnDraw(Context paramContext,
						   AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init(paramContext);
	}

	/**
	 * 初始化.9.png
	 *
	 * @param paramContext
	 */
	private void init(Context paramContext) {
		// 初始化.9.png
		setNinePatchBitMap(R.drawable.widget_focus);
		// 设置背景颜色为透明
		setBackgroundColor(0);
	}

	/**
	 * 初始化.9.png
	 *
	 * @param paramContext
	 */
	private void setNinePatchBitMap(int drawableId) {
		try {
			if (this.ninePatchMap == null) {
				this.ninePatchMap = new HashMap<Integer, NinePatch>();
			}
			if (this.ninePatchMap.containsKey(Integer.valueOf(drawableId))) {
				this.ninePatch = ((NinePatch) this.ninePatchMap.get(Integer
						.valueOf(drawableId)));
			} else {
				Bitmap localBitmap = BitmapFactory.decodeResource(
						getResources(), drawableId);
				this.bitmapWidth = localBitmap.getWidth();
				this.bitmapHeight = localBitmap.getHeight();
				this.ninePatch = new NinePatch(localBitmap,
						localBitmap.getNinePatchChunk(), null);
				this.ninePatchMap.put(Integer.valueOf(drawableId),
						this.ninePatch);
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
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
			this.drawViewLeft = (this.drawArg.originLeft + getSinRealTimeLength(
					this.count, this.drawArg.horizontalDistance));
			// 原始top位置+竖直位移
			this.drawViewTop = (this.drawArg.originTop + getSinRealTimeLength(
					this.count, this.drawArg.verticalDistance));
			//
			this.drawViewBottom = (this.drawViewTop
					+ getSinRealTimeLength(this.count, this.drawArg.heightDelta) + this.drawArg.originHeight);
			this.drawViewRight = (this.drawViewLeft
					+ getSinRealTimeLength(this.count, this.drawArg.widthDelta) + this.drawArg.originWidth);
			// 绘制下一帧
			invalidate();
		}

		/**
		 * 绘制焦点框
		 */
		Rect localRect = new Rect(this.drawViewLeft, this.drawViewTop,
				this.drawViewRight, this.drawViewBottom);
		this.ninePatch.draw(canvas, localRect);
		//
		if ((this.listener != null) && (this.count == this.duration)) {
			this.listener.endMove();
		}

	}

	/**
	 * 移动到目标View
	 *
	 * @param targetViewLocation
	 *            目标View左上角坐标
	 * @param targetViewWH
	 *            目标View的宽高
	 * @param paramInt
	 *
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
				// // 移动焦点框时，没有有动画(直接一帧跳过去)
				this.drawViewLeft = targetViewLocation[0];
				this.drawViewTop = targetViewLocation[1];
				//
				this.drawViewRight = (this.drawViewLeft + targetViewWH[0]);
				this.drawViewBottom = (this.drawViewTop + targetViewWH[1]);
				this.drawArg = null;
				//
				break;

			case SHOW_SCALE_ANIMATION:
				// 重置count
				this.count = 0;
				//
				this.drawArg = new DrawArg();
				// 水平与竖直距离
				this.drawArg.horizontalDistance = (-getHalfLength(targetViewWH[0]) + getHalfLength(this.bitmapWidth));
				this.drawArg.verticalDistance = (-getHalfLength(targetViewWH[1]) + getHalfLength(this.bitmapHeight));
				/**
				 * 焦点框要宽、高要增大的距离
				 */
				this.drawArg.widthDelta = (targetViewWH[0] - this.bitmapWidth);
				this.drawArg.heightDelta = (targetViewWH[1] - this.bitmapHeight);
				/**
				 * 以目标View为中心，确定BitMap的左上点、右下点坐标
				 */
				this.drawArg.originLeft = (targetViewLocation[0]
						+ getHalfLength(targetViewWH[0]) - getHalfLength(this.bitmapWidth));
				//
				this.drawArg.originTop = (targetViewLocation[1]
						+ getHalfLength(targetViewWH[1]) - getHalfLength(this.bitmapHeight));
				// 原始图片的高
				this.drawArg.originHeight = this.bitmapHeight;
				// 原始图片的宽
				this.drawArg.originWidth = this.bitmapWidth;

				break;

			case SHOW_TRANSLATE_AND_SCALE_ANIMATION:
				// 重置count
				this.count = 0;
				//
				this.drawArg = new DrawArg();
				/**
				 * 水平与竖直距离。目标位置-当前View的位置
				 */
				this.drawArg.horizontalDistance = (targetViewLocation[0] - this.drawViewLeft);
				this.drawArg.verticalDistance = (targetViewLocation[1] - this.drawViewTop);
				/**
				 * 目标宽高 - 当前宽高
				 */
				this.drawArg.widthDelta = (targetViewWH[0] - (this.drawViewRight - this.drawViewLeft));
				this.drawArg.heightDelta = (targetViewWH[1] - (this.drawViewBottom - this.drawViewTop));
				//
				this.drawArg.originLeft = this.drawViewLeft;
				this.drawArg.originTop = this.drawViewTop;
				this.drawArg.originHeight = (this.drawViewBottom - this.drawViewTop);
				this.drawArg.originWidth = (this.drawViewRight - this.drawViewLeft);
				//
				break;
		}
		// 刷新界面
		invalidate();
	}

	/**
	 * 获取远点位置坐标
	 *
	 * @param length
	 * @return
	 */
	private int getHalfLength(int length) {
		if (length % 2 == 0) {
			return length / 2;
		}
		return (length + 1) / 2;

	}

	/**
	 * 正弦函数进行数值变化
	 *
	 * @param count
	 *            第几帧
	 * @param distance
	 *            要移动的距离
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
	 * 重新设置
	 */
	public void reset() {
		setNinePatchBitMap(R.drawable.widget_focus);
		this.bitMapWhiteLength = 26;
	}

	/**
	 * 留白的调节值
	 *
	 * @param delta
	 */
	public void setwhiteLengthOffect(int whiteLengthOffect) {
		this.whiteLengthOffect = whiteLengthOffect;
	}

	/**
	 * 移动到目标View
	 *
	 * @param destinView
	 *            目标View
	 * @param animationType
	 *            动画类型
	 */
	public void moveToDestin(final View destinView, final int animationType) {
		if (destinView == null) {
			return;
		}

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
	 * @param destinView
	 *            目标类型
	 * @param animationType
	 *            动画类型
	 * @param drawableId
	 *            图片id
	 * @param whiteLength
	 *            留白大小
	 */
	public void moveToDestin(View destinView, int animationType,
							 int drawableId, int whiteLength) {
		setNinePatchBitMap(drawableId);
		this.bitMapWhiteLength = whiteLength;
		moveToDestin(destinView, animationType);
	}

	/**
	 * 移动到目标View
	 *
	 * @param targetViewLocation
	 *            目标View的Location
	 * @param targetViewWH
	 *            目标View的宽高
	 * @param annimationType
	 *            目标动画的类型
	 * @param drawableId
	 *            动画图片
	 * @param bitMapWhiteLength
	 *            留白大小
	 */
	public void moveToDestin(int[] targetViewLocation, int[] targetViewWH,
							 int annimationType, int drawableId, int bitMapWhiteLength) {
		setNinePatchBitMap(drawableId);
		this.bitMapWhiteLength = bitMapWhiteLength;
		MoveViewToPosition(targetViewLocation, targetViewWH, annimationType);
	}

	/**
	 *
	 * @param destinViewLocation
	 *            目标View的宽高
	 * @param destinViewWH2
	 *            目标View的宽高
	 * @param animationType
	 *            动画类型
	 */
	private void moveToDestin(int[] destinViewLocation, int[] destinViewWH,
							  int animationType) {

		// 当前View的宽高值
		if (destinViewWH == null) {
			destinViewWH = new int[2];
			destinViewWH[0] = (this.drawViewRight - this.drawViewLeft);
			destinViewWH[1] = (this.drawViewBottom - this.drawViewTop);
		}

		/**
		 * 如果有留白
		 */
		if (this.whiteLengthOffect > 0) {
			destinViewLocation[0] -= this.whiteLengthOffect;
			destinViewLocation[1] -= this.whiteLengthOffect;
			destinViewWH[0] += 2 * this.whiteLengthOffect;
			destinViewWH[1] += 2 * this.whiteLengthOffect;
		}

		/**
		 * 获取相对父布局的位置
		 */
		getRelativePositionInParent(destinViewLocation);
		// 加上留白
		destinViewLocation[0] -= this.bitMapWhiteLength;
		destinViewLocation[1] -= this.bitMapWhiteLength;
		// 加上留白
		destinViewWH[0] += 2 * this.bitMapWhiteLength;
		destinViewWH[1] += 2 * this.bitMapWhiteLength;

		/**
		 * 开始移动
		 */
		MoveViewToPosition(destinViewLocation, destinViewWH, animationType);

	}

	/**
	 *
	 * @param drawableId
	 * @param bitMapWhiteLength
	 *            留白大小
	 */
	private void setNinePatchBitMap(int drawableId, int bitMapWhiteLength) {
		if (this.ninePatchMap != null) {
			this.ninePatchMap.clear();
			this.ninePatchMap = null;
		}
		Bitmap localBitmap = BitmapFactory.decodeResource(getResources(),
				drawableId);
		//
		this.bitmapWidth = localBitmap.getWidth();
		this.bitmapHeight = localBitmap.getHeight();
		//
		this.ninePatch = new NinePatch(localBitmap,
				localBitmap.getNinePatchChunk(), null);
		//
		this.bitMapWhiteLength = bitMapWhiteLength;
	}

	/**
	 * 显示View
	 */
	public void setVisible() {
		if (getVisibility() == View.VISIBLE)
			return;
		setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏
	 */
	public void setInvisible() {
		if (getVisibility() == View.INVISIBLE)
			return;
		setVisibility(View.INVISIBLE);
	}

	/**
	 *
	 * @author xiaxueliang
	 *
	 */
	private static class DrawArg {
		//
		int originHeight;
		int originLeft;
		int originTop;
		int originWidth;
		//
		int heightDelta;
		int widthDelta;
		//
		int horizontalDistance;
		int verticalDistance;
	}

	/**
	 * 开启移动，结束移动的Listener
	 *
	 * @author xiaxueliang
	 *
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