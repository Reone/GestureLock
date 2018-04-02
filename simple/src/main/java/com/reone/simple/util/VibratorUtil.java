package com.reone.simple.util;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * 震动工具
 */
public class VibratorUtil {
    private Vibrator vib;
    public VibratorUtil(Context context){
        vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
    }

    /**
     * 震动
     * @param milliseconds 毫秒数
     */
    public void vibrate(long milliseconds){
        cancel();
        vib.vibrate(milliseconds);
    }

    public void cancel(){
        vib.cancel();
    }
}
