package com.reone.gesturelibrary.style;

import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by wangxingsheng on 2018/4/3.
 *
 */

public class Style {
    public static BaseStyle NORMAL = new BaseStyle() {
        @Override
        public boolean drawCircle(RectF oval, Paint paint) {
            return true;
        }
    };
}
