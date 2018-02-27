package com.reone.gesturelibrary.process;

import android.os.Handler;

import com.reone.gesturelibrary.entity.Point;
import com.reone.gesturelibrary.util.Base64;
import com.reone.gesturelibrary.util.PasswordCache;
import com.reone.gesturelibrary.util.VibratorUtil;
import com.reone.gesturelibrary.view.LockView;

import java.util.Arrays;
import java.util.List;

/**
 * 逻辑管理
 */
public class ProcessManager {
    /**
     * 设置密码
     */
    public static final int SETTING_PASSWORD = 1;
    /**
     * 修改密码
     */
    public static final int EDIT_PASSWORD = 2;
    /**
     * 验证密码
     */
    public static final int VERIFY_PASSWORD = 3;
    /**
     * 清除密码
     */
    public static final int CLEAR_PASSWORD = 4;

    private LockView lockView;

    //监听
    private OnCompleteListener mCompleteListener;
    //验证或者设置 0:设置 1:验证
    private int mode = SETTING_PASSWORD;
    //需要输入回调
    private boolean needInputComplete = false;
    //编辑密码前是否验证
    private boolean isEditVerify = false;

    //错误限制 默认为4次
    private int errorNumber = 4;
    //记录上一次滑动的密码
    private String oldPassword = null;
    //记录当前第几次触发 默认为0次
    private int showTimes = 0;

    //当前密码是否正确 默认为正确
    private boolean isCorrect = true;
    //密码最小长度
    private int passwordMinLength = 3;
    //是否保存保存PIN
    private boolean isSavePin = false;

    //画完的图像消失的时间
    private Long mResetTime = 500L;
    /**
     * 是否需要震动
     */
    private boolean needVibrator = true;

    private PasswordCache passwordCache;
    private VibratorUtil vibratorUtil;

    //用于执行清除界面
    private Handler handler = new Handler();
    //用于定时执行清除界面
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(run);
            lockView.reset();
            lockView.postInvalidate();
        }
    };

    public ProcessManager(LockView lockView){
        this.lockView = lockView;
        passwordCache = new PasswordCache(lockView.getContext());
        vibratorUtil = new VibratorUtil(lockView.getContext());
    }

    public void actionFinish(List<Point> points) {
        handler.postDelayed(run, mResetTime);
        if (mCompleteListener != null && needInputComplete && points.size()>0) {
            int[] indexs = new int[points.size()];
            for (int i = 0; i < points.size(); i++) {
                indexs[i] = points.get(i).index;
            }
            mCompleteListener.onInputComplete(Arrays.toString(indexs));
        }
        if (points.size() == 1) {
            lockView.reset();
            return;
        }
        if (points.size() < getPasswordMinLength()
                && points.size() > 0) {
            lockView.error();
            if (mCompleteListener != null) {
                mCompleteListener.onPasswordIsShort(getPasswordMinLength());  //密码太短
            }
            return;
        }
        if (points.size() >= getPasswordMinLength()) {
            int[] indexs = new int[points.size()];
            for (int i = 0; i < points.size(); i++) {
                indexs[i] = points.get(i).index;
            }
            if (mode == SETTING_PASSWORD || isEditVerify) {
                invalidSettingPass(Base64.encryptionString(indexs), indexs);
            }else {
                if(mode == VERIFY_PASSWORD){
                    setOldPassword(passwordCache.password());
                }
                onVerifyPassword(Base64.encryptionString(indexs), indexs);
            }
        }
    }

    /**
     * 验证设置密码，滑动两次密码是否相同
     *
     * @param password
     */
    private void invalidSettingPass(String password, int[] indexs) {
        if (showTimes == 0) {
            oldPassword = password;
            if (mCompleteListener != null) {
                mCompleteListener.onAgainInputPassword(mode, password, indexs);
            }
            showTimes++;
            lockView.reset();
        } else if (showTimes == 1) {
            onVerifyPassword(password, indexs);
        }
    }

    /**
     * 验证本地密码与当前滑动密码是否相同
     *
     * @param indexs
     */
    private void onVerifyPassword(String password, int[] indexs) {
        isCorrect = password!=null && !password.isEmpty() && oldPassword!=null && !oldPassword.isEmpty() && oldPassword.equals(password);
        if (!isCorrect) {
            drawPassWordError();
        } else {
            drawPassWordRight(password, indexs);
        }
    }

    /**
     * 密码输入错误回调
     */
    private void drawPassWordError() {
        if (mCompleteListener == null) {
            return;
        }
        if (mode == SETTING_PASSWORD) {
            mCompleteListener.onEnteredPasswordsDiffer();
        } else if (mode == EDIT_PASSWORD && isEditVerify) {
            mCompleteListener.onEnteredPasswordsDiffer();
        } else {
            errorNumber--;
            if (errorNumber <= 0) {
                mCompleteListener.onErrorNumberMany();
            } else {
                mCompleteListener.onError(errorNumber + "");
            }
        }
        lockView.error();
        lockView.postInvalidate();
    }


    /**
     * 输入密码正确相关回调
     *
     * @param indexs
     * @param password
     */
    private void drawPassWordRight(String password, int[] indexs) {
        if (mCompleteListener == null) {
            return;
        }
        if (mode == EDIT_PASSWORD && !isEditVerify) {//修改密码，旧密码正确，进行新密码设置
            mCompleteListener.onInputNewPassword();
            isEditVerify = true;
            showTimes = 0;
            return;
        }
        if (mode == EDIT_PASSWORD && isEditVerify) {
            savePassWord(password);
        } else if (mode == CLEAR_PASSWORD) {//清除密码
            passwordCache.remove();
        } else if (mode == SETTING_PASSWORD) {//完成密码设置，存储到本地
            savePassWord(password);
        } else {
            isEditVerify = false;
        }
        mCompleteListener.onComplete(password, indexs);
    }

    /**
     * 存储密码到本地
     *
     * @param password
     */
    private void savePassWord(String password) {
        if (isSavePin) {
            passwordCache.save(password);
        }
    }

    /**
     * 设置监听
     *
     */
    public void setOnCompleteListener(OnCompleteListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }

    public OnCompleteListener getOnCompleteListener(){
        return mCompleteListener;
    }

    public void newPoint() {
        if(needVibrator){
            vibratorUtil.vibrate(10);
        }
    }

    public boolean isNeedVibrator() {
        return needVibrator;
    }

    public void setNeedVibrator(boolean needVibrator) {
        this.needVibrator = needVibrator;
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    //设置允许最大输入错误次数
    public void setErrorNumber(int errorNumber) {
        this.errorNumber = errorNumber;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    //设置已经设置过的密码，验证密码时需要用到
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public int getPasswordMinLength() {
        return passwordMinLength;
    }

    //设置密码最少输入长度
    public void setPasswordMinLength(int passwordMinLength) {
        this.passwordMinLength = passwordMinLength;
    }

    public void setNeedInputComplete(boolean bool){
        this.needInputComplete = bool;
    }

    public boolean isNeedInputComplete(){
        return this.needInputComplete;
    }


    //设置解锁模式
    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public boolean isSavePin() {
        return isSavePin;
    }

    //设置密码后是否保存到本地
    public void setSavePin(boolean savePin) {
        isSavePin = savePin;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public void removeCallbacks() {
        handler.removeCallbacks(run);
    }

    public Long getResetTime() {
        return mResetTime;
    }

    public void setResetTime(Long mResetTime) {
        this.mResetTime = mResetTime;
    }
}
