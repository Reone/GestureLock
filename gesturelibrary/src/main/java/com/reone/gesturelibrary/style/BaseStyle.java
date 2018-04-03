package com.reone.gesturelibrary.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by wangxingsheng on 2018/4/3.
 *
 */

public interface BaseStyle {
    /**
     * 如果返回true，则会以自定的方式绘制
     * @param oval 圆的位置
     * @param paint 已经设置好属性的画笔
     * @return boolean 如果返回true，则会以自定的方式绘制；返回false，此方法不起作用
     */
    boolean drawCircle(Canvas canvas,RectF oval, Paint paint);
}
