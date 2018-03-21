package com.reone.gesturelibrary.process;

import android.support.annotation.NonNull;

import com.reone.gesturelibrary.view.LockView;

import java.util.List;

/**
 * Created by wangxingsheng on 2018/3/12.
 * 九宫锁的输入监听，流程管理
 */

public abstract class ProcessManager {

    private LockView lockView = null;

    /**
     * 开始输入
     * @return true则允许输入，false不允许输入
     */
    public abstract boolean onInputStart();

    /**
     * 当一个新的点选中的时候调用
     * @param point
     */
    public abstract void pointAttach(@NonNull Integer point);

    /**
     * 输入完成
     * @param points
     */
    public abstract void onInputEnd(@NonNull List<Integer> points);

    /**
     * 某些变量的初始化需要用到lockView可以在这个方法中进行
     */
    public abstract void lockViewAttach();

    protected @NonNull LockView getLockView() {
        return lockView;
    }

    public void setLockView(@NonNull LockView lockView) {
        this.lockView = lockView;
        lockViewAttach();
    }
}
