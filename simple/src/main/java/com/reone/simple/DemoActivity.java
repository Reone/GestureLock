package com.reone.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.reone.gesturelibrary.process.OnCompleteListener;
import com.reone.gesturelibrary.util.PasswordCache;
import com.reone.gesturelibrary.view.LockView;
import com.reone.gesturelibrary.process.SimpleProcessManager;

import java.util.List;

/**
 * Created by wangxingsheng on 2018/2/28.
 *
 */

public class DemoActivity extends AppCompatActivity{
    private LockView<SimpleProcessManager> lockView;
    private View clearBtn,checkBtn;
    private RadioGroup styleRadio;
    private PasswordCache passwordCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        passwordCache = new PasswordCache(this);
        lockView = findViewById(R.id.lockView);
        clearBtn = findViewById(R.id.clear);
        checkBtn = findViewById(R.id.check);
        styleRadio = findViewById(R.id.style);
        initLockView();
        initBtn();
    }

    private void initBtn() {
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordCache.remove();
                loadStatu();
            }
        });
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadStatu();
            }
        });
        styleRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.style_normal:
                        break;
                    case R.id.style_rect:
                        break;
                    case R.id.style_customize:
                        break;
                }
            }
        });
    }

    /**
     * 设置组件
     */
    private void initLockView(){
        //设置流程管理器
        lockView.setProcessManager(new SimpleProcessManager());
        //设置组件状态
        lockView.getProcessManager().setMode(SimpleProcessManager.SETTING_PASSWORD);
        //显示绘制方向
        lockView.setShow(true);
        //允许最大输入次数
        lockView.getProcessManager().setErrorNumber(5);
        //密码最少位数
        lockView.getProcessManager().setPasswordMinLength(4);
        //编辑密码或设置密码时，是否将密码保存到本地，配合setSaveLockKey使用
        lockView.getProcessManager().setSavePin(true);
        //设置输入回调是否可用
        lockView.getProcessManager().setNeedInputComplete(true);
        //设置流程监听
        lockView.getProcessManager().setOnCompleteListener(mCompleteListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatu();
    }

    /**
     * 加载lockView的状态
     */
    private void loadStatu(){
        if(passwordCache.password() == null || passwordCache.password().isEmpty()){
            lockView.getProcessManager().setMode(SimpleProcessManager.SETTING_PASSWORD);
            setHint("请设置新的手势密码");
        }else{
            lockView.getProcessManager().setMode(SimpleProcessManager.VERIFY_PASSWORD);
            setHint("请绘制手势密码");
        }
    }

    private void setHint(String hint) {
        setTitle(hint);
    }

    private OnCompleteListener mCompleteListener = new OnCompleteListener() {
        @Override
        public void onInputComplete(String password) {
            Toast.makeText(DemoActivity.this,password,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(String password, List<Integer> indexs) {
            switch (lockView.getProcessManager().getMode()){
                case SimpleProcessManager.SETTING_PASSWORD:
                    setHint("密码设置成功");
                    break;
                case SimpleProcessManager.VERIFY_PASSWORD:
                    setHint("密码验证成功");
                    break;
            }
        }

        @Override
        public void onError(String errorTimes) {
            setHint("密码错误，还可以输入"+errorTimes+"次");
        }

        @Override
        public void onPasswordIsShort(int passwordMinLength) {
            setHint("密码不能少于"+passwordMinLength+"个点");
        }

        @Override
        public void onAgainInputPassword(int mode, String password, List<Integer> indexs) {
            setHint("请再次输入密码");
        }

        @Override
        public void onInputNewPassword() {
            setHint("请输入新密码");
        }

        @Override
        public void onEnteredPasswordsDiffer() {
            setHint("两次输入的密码不一致");
        }

        @Override
        public void onErrorNumberMany() {
            setHint("密码错误次数超过限制，不能再输入");
        }
    };

}
