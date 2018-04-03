package com.reone.gesturelibrary.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by wangxingsheng on 2018/4/3.
 *
 */

public class Style {
    public static BaseStyle NORMAL = new BaseStyle() {
        @Override
        public boolean drawCircle(Canvas canvas, RectF oval, Paint paint) {
            canvas.drawCircle(oval.centerX(),oval.centerY(),oval.width()/2,paint);
            return true;
        }
    };
    public static BaseStyle RECT = new BaseStyle() {
        @Override
        public boolean drawCircle(Canvas canvas, RectF oval, Paint paint) {
            canvas.drawRect(oval,paint);
            return true;
        }
    };
}
