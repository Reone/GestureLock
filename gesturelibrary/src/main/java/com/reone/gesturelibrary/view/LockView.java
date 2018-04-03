package com.reone.gesturelibrary.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.reone.gesturelibrary.R;
import com.reone.gesturelibrary.process.ProcessManager;
import com.reone.gesturelibrary.entity.Point;
import com.reone.gesturelibrary.style.BaseStyle;
import com.reone.gesturelibrary.util.LockUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 手势解锁
 */
public class LockView<T extends ProcessManager> extends View {

    //控件宽度
    private float width = 0;
    //控件高度
    private float height = 0;
    //是否已缓存
    private boolean isCache = false;
    //画笔
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //九宫格的圆
    private Point[][] mPoints = new Point[3][3];
    //选中圆的集合
    private List<Point> sPoints = new ArrayList<>();
    //判断是否正在绘制并且未到达下一个点
    private boolean movingNoPoint = false;
    //正在移动的x,y坐标
    float movingX, movingY;

    //判断是否触摸屏幕
    private boolean checking = false;

    //是否显示滑动方向 默认为显示
    private boolean isShow = false;

    //普通状态下圈的颜色
    private int mColorUpRing = 0xFF378FC9;
    //按下时圈的颜色
    private int mColorOnRing = 0xFF378FC9;
    //松开手时的颜色
    private int mColorErrorRing = 0xFF378FC9;
    //按下时圈内填充颜色
    private int mColorOnBackground = 0xFFFFFF;

    //外圈大小
    private float mOuterRingWidth = 120;
    //内圆大小
    private float mInnerRingWidth = mOuterRingWidth / 3;
    //内圆间距
    private float mCircleSpacing;
    //圆圈半径
    private float mRadius;
    //小圆半径
    private float mInnerRingRadius;
    //小圆半透明背景半径
    private float mInnerBackgroundRadius;
    //内圆背景大小（半透明内圆）
    private float mInnerBackgroundWidth;
    //三角形边长
    private float mArrowLength;
    //未按下时圆圈的边宽
    private int mNoFingerStrokeWidth = 6;
    //按下时圆圈的边宽
    private int mOnStrokeWidth = 8;
    //连线边款
    private int mLineWidth = 8;

    private T processManager;

    private BaseStyle style;

    public LockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.GestureLock_styleable, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.GestureLock_styleable_color_on_ring) {
                mColorOnRing = a.getColor(attr, mColorOnRing);
            } else if (attr == R.styleable.GestureLock_styleable_color_up_ring) {
                mColorUpRing = a.getColor(attr, mColorUpRing);
            } else if (attr == R.styleable.GestureLock_styleable_color_error_ring) {
                mColorErrorRing = a.getColor(attr, mColorErrorRing);
            } else if (attr == R.styleable.GestureLock_styleable_inner_ring_width) {
                mInnerRingWidth = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.GestureLock_styleable_outer_ring_spacing_width) {
                mCircleSpacing = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.GestureLock_styleable_inner_background_width) {
                mInnerBackgroundWidth = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.GestureLock_styleable_line_width) {
                mLineWidth = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.GestureLock_styleable_stroke_width) {
                mOnStrokeWidth = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.GestureLock_styleable_no_finger_stroke_width) {
                mNoFingerStrokeWidth = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.GestureLock_styleable_color_on_background) {
                mColorOnBackground = a.getColor(attr, mColorOnBackground);
            }
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = measure(widthMeasureSpec);
        height = measure(heightMeasureSpec);
        width = height = Math.min(width, height);
    }

    /**
     * 获取控件尺寸
     */
    private static int measure(int origin) {
        int result = 400;
        int specMode = View.MeasureSpec.getMode(origin);
        int specSize = View.MeasureSpec.getSize(origin);
        if (specMode == View.MeasureSpec.EXACTLY) {//控件设定了固定的高度
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {//控件没有设定固定宽高，wrap_content
            result = Math.min(specSize, 400);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isCache) {
            initCache();
        }
        //绘制圆以及显示当前状态
        drawToCanvas(canvas);
    }

    /**
     * 初始化Cache信息
     */
    private void initCache() {

        initGestureLockViewWidth();
        int strokeWidth = Math.max(mOnStrokeWidth,mNoFingerStrokeWidth);
        float[] xs = {mRadius + strokeWidth/2,width / 2,width - strokeWidth/2 - mRadius};
        float[] ys = {mRadius + strokeWidth/2,height / 2,height - strokeWidth/2 - mRadius};

        for (int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                mPoints[i][j] = new Point(xs[j],ys[i]);
            }
        }
        int k = 0;
        for (Point[] ps : mPoints) {
            for (Point p : ps) {
                p.index = k;
                k++;
            }
        }
        isCache = true;
    }

    /**
     * 计算圆以及连接线的尺寸
     */
    private void initGestureLockViewWidth() {
        if (mCircleSpacing == 0) {
            initCircleSpacing();
        } else {
            float mSpacing = mCircleSpacing * 2;
            mOuterRingWidth = (width - mSpacing) / 3;
        }
        if (mOuterRingWidth <= 0) {//防止手动设置圆圆之间间距过大问题
            initCircleSpacing();
        }
        if (mInnerRingWidth == 0 || mInnerRingWidth >= mOuterRingWidth) {
            mInnerRingWidth = mOuterRingWidth / 3;
        }
        if (mInnerBackgroundWidth == 0 || mInnerBackgroundWidth >= mOuterRingWidth) {
            mInnerBackgroundWidth = mInnerRingWidth * 1.3f;
        }
        mInnerBackgroundRadius = mInnerBackgroundWidth / 2;
        mRadius = (mOuterRingWidth - Math.max(mOnStrokeWidth,mNoFingerStrokeWidth)) / 2;
        mInnerRingRadius = mInnerRingWidth / 2;
        mArrowLength = mRadius * 0.25f;//三角形的边长
    }

    /**
     * 当外圈间距没有设置时，初始化外圆之间的间距
     */
    private void initCircleSpacing() {
        // 计算每个GestureLockView的宽度
        mOuterRingWidth = width / 6;
        //计算每个GestureLockView的间距
        mCircleSpacing = (width - mOuterRingWidth * 3) / 2;
    }

    /**
     * 图像绘制
     */
    private void drawToCanvas(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        // 画连线
        drawAllLine(canvas);
        // 画所有点
        drawAllPoint(canvas);
        // 是否绘制方向图标
        if (isShow()) {
            drawDirectionArrow(canvas);
        }
    }

    /**
     * 绘制解锁连接线
     *
     */
    private void drawAllLine(Canvas canvas) {
        if (sPoints.size() > 0) {
            Point tp = sPoints.get(0);
            for (int i = 1; i < sPoints.size(); i++) {
                //根据移动的方向绘制线
                Point p = sPoints.get(i);
                if (p.state == Point.STATE_CHECK_ERROR) {
                    drawErrorLine(canvas, tp, p);
                } else {
                    drawLine(canvas, tp, p);
                }
                tp = p;
            }
            if (this.movingNoPoint) {
                //到达下一个点停止移动绘制固定的方向
                drawLine(canvas, tp, new Point((int) movingX + 20, (int) movingY));
            }
        }
    }

    /**
     * 绘制解锁图案所有的点
     *
     */
    private void drawAllPoint(Canvas canvas) {
        for (Point[] mPoint : mPoints) {
            for (Point p : mPoint) {
                if (p != null) {
                    if (p.state == Point.STATE_CHECK) {
                        onDrawOn(canvas, p);
                    } else if (p.state == Point.STATE_CHECK_ERROR) {
                        onDrawError(canvas, p);
                    } else {
                        onDrawNoFinger(canvas, p);
                    }
                }
            }
        }
    }


    /**
     * 绘制解锁图案连接的方向
     *
     */
    private void drawDirectionArrow(Canvas canvas) {
        // 绘制方向图标
        if (sPoints.size() <= 0) {
            return;
        }
        Point tp = sPoints.get(0);
        for (int i = 1; i < sPoints.size(); i++) {
            //根据移动的方向绘制方向图标
            Point p = sPoints.get(i);
            if (p.state == Point.STATE_CHECK_ERROR) {
                drawDirectionArrow(canvas, tp, p, mColorErrorRing);
            } else {
                drawDirectionArrow(canvas, tp, p, mColorOnRing);
            }
            tp = p;
        }
    }

    /**
     * 绘制按下时状态
     *
     */
    private void onDrawOn(Canvas canvas, Point p) {
        // 绘制背景
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColorOnBackground);
        canvas.drawCircle(p.x, p.y, mRadius, mPaint);
        // 绘制外圆
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColorOnRing);
        mPaint.setStrokeWidth(mOnStrokeWidth);
        drawCircle(canvas, p);
        // 绘制内圆背景
        onDrawInnerCircleBackground(canvas, p, mColorOnRing);
        // 绘制内圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColorOnRing);
        canvas.drawCircle(p.x, p.y, mInnerRingRadius, mPaint);
    }

    /**
     * 画圈
     */
    private void drawCircle(Canvas canvas, Point p) {
        RectF oval = new RectF(p.x-mRadius,p.y-mRadius,p.x+mRadius,p.y+mRadius);
        if(style == null || !style.drawCircle(canvas,oval,mPaint)){
            canvas.drawArc(oval,-135f,315f,false,mPaint);
            canvas.drawArc(oval,-165f,16f,false,mPaint);
        }
    }

    /**
     * 绘制松开手时状态
     *
     */
    private void onDrawError(Canvas canvas, Point p) {
        // 绘制背景
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColorOnBackground);
        canvas.drawCircle(p.x, p.y, mRadius, mPaint);
        // 绘制圆圈
        mPaint.setColor(mColorErrorRing);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mOnStrokeWidth);
        drawCircle(canvas, p);
        // 绘制内圆背景
        onDrawInnerCircleBackground(canvas, p, mColorErrorRing);
        // 绘制内圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColorErrorRing);
        canvas.drawCircle(p.x, p.y, mInnerRingRadius, mPaint);
    }

    /**
     * 绘制普通状态
     *
     */
    private void onDrawNoFinger(Canvas canvas, Point p) {
        // 绘制外圆
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColorUpRing);
        mPaint.setStrokeWidth(mNoFingerStrokeWidth);
        drawCircle(canvas, p);
    }

    /**
     * 绘制内圆透明背景
     *
     */
    private void onDrawInnerCircleBackground(Canvas canvas, Point p, int color) {
        // 绘制内圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
        mPaint.setAlpha(100);
        canvas.drawCircle(p.x, p.y, mInnerBackgroundRadius, mPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(processManager!=null && !processManager.onInputStart()){
            return false;
        }
        movingNoPoint = false;
        float ex = event.getX();
        float ey = event.getY();
        boolean isFinish = false;
        Point p = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 点下
                // 如果正在清除密码,则取消
                p = actionDown(ex, ey);
                break;
            case MotionEvent.ACTION_MOVE: // 移动
                p = actionMove(ex, ey);
                break;
            case MotionEvent.ACTION_UP: // 提起
                p = checkSelectPoint(ex, ey);
                checking = false;
                isFinish = true;
                break;
            default:
                movingNoPoint = true;
                break;
        }
        if (!isFinish && checking && p != null) {
            int rk = crossPoint(p);
            if (rk == 2) {
                //与非最后一重叠
                movingNoPoint = true;
                movingX = ex;
                movingY = ey;
            } else if (rk == 0) {
                //一个新点
                p.state = Point.STATE_CHECK;
                addPoint(p);
            }
        }
        if (isFinish) {
            actionFinish();
        }
        postInvalidate();
        return true;
    }

    /**
     * 解锁图案绘制完成
     */
    private void actionFinish() {
        ArrayList<Integer> points = new ArrayList<>();
        for (Point point:sPoints) {
            points.add(point.index);
        }
        if(processManager!=null){
            processManager.onInputEnd(points);
        }
    }

    /**
     * 按下
     *
     */
    private Point actionDown(float ex, float ey) {
        // 删除之前的点
        reset();
        Point p = checkSelectPoint(ex, ey);
        if (p != null) {
            checking = true;
        }
        return p;
    }

    /**
     * 移动
     *
     */
    private Point actionMove(float ex, float ey) {
        Point p = null;
        if (checking) {
            p = checkSelectPoint(ex, ey);
            if (p == null) {
                movingNoPoint = true;
                movingX = ex;
                movingY = ey;
            }
        }
        return p;
    }

    /**
     * 向选中点集合中添加一个点
     *
     */
    private void addPoint(Point point) {
        this.sPoints.add(point);
        if(processManager!=null){
            processManager.pointAttach(point.index);
        }


    }

    /**
     * 检查点是否被选择
     *
     */
    private Point checkSelectPoint(float x, float y) {
        for (Point[] mPoint : mPoints) {
            for (Point p : mPoint) {
                if (LockUtil.checkInRound(p.x, p.y, mRadius, (int) x, (int) y)) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * 判断点是否有交叉 返回 0,新点 ,1 与上一点重叠 2,与非最后一点重叠
     *
     */
    private int crossPoint(Point p) {
        // 重叠的不最后一个则 reset
        if (sPoints.contains(p)) {
            if (sPoints.size() > 2) {
                // 与非最后一点重叠
                if (sPoints.get(sPoints.size() - 1).index != p.index) {
                    return 2;
                }
            }
            return 1; // 与最后一点重叠
        } else {
            return 0; // 新点
        }
    }

    /**
     * 重置点状态
     */
    public void reset() {
        for (Point p : sPoints) {
            p.state = Point.STATE_NORMAL;
        }
        sPoints.clear();
    }

    /**
     * 画两点的连接
     *
     */
    private void drawLine(Canvas canvas, Point a, Point b) {
        mPaint.setColor(mColorOnRing);
        mPaint.setStrokeWidth(mLineWidth);
        canvas.drawLine(a.x, a.y, b.x, b.y, mPaint);
    }

    /**
     * 错误线
     *
     */
    private void drawErrorLine(Canvas canvas, Point a, Point b) {
        mPaint.setColor(mColorErrorRing);
        mPaint.setStrokeWidth(mLineWidth);
        canvas.drawLine(a.x, a.y, b.x, b.y, mPaint);
    }

    /**
     * 绘制方向图标,三角形指示标
     *
     */
    private void drawDirectionArrow(Canvas canvas, Point a, Point b, int color) {
        //获取角度
        float degrees = LockUtil.getDegrees(a, b) + 90;
        //根据两点方向旋转
        canvas.rotate(degrees, a.x, a.y);
        drawArrow(canvas, a, color);
        //旋转方向
        canvas.rotate(-degrees, a.x, a.y);
    }

    /**
     * 绘制三角形指示标
     *
     */
    private void drawArrow(Canvas canvas, Point a, int color) {
        // 绘制三角形，初始时是个默认箭头朝上的一个等腰三角形，用户绘制结束后，根据由两个GestureLockView决定需要旋转多少度
        Path mArrowPath = new Path();
        float offset = mInnerBackgroundRadius + (mArrowLength + mRadius - mInnerBackgroundRadius) / 2;//偏移量,定位三角形位置
        mArrowPath.moveTo(a.x, a.y - offset);
        mArrowPath.lineTo(a.x - mArrowLength, a.y - offset
                + mArrowLength);
        mArrowPath.lineTo(a.x + mArrowLength, a.y - offset
                + mArrowLength);
        mArrowPath.close();
        mArrowPath.setFillType(Path.FillType.WINDING);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
        canvas.drawPath(mArrowPath, mPaint);
    }

    /**
     * 设置已经选中的为错误
     */
    public void error() {
        for (Point p : sPoints) {
            p.state = Point.STATE_CHECK_ERROR;
        }
    }

    public boolean isShow() {
        return isShow;
    }

    //是否显示连接方向
    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

    public void setProcessManager(T processManager){
        this.processManager = processManager;
        processManager.setLockView(this);
    }

    public T getProcessManager(){
        return processManager;
    }

    public void setStyle(BaseStyle style) {
        this.style = style;
        postInvalidate();
    }
}
