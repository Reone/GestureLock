package com.reone.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.reone.gesturelibrary.view.LockView;
import com.reone.simple.manager.SimpleProcessManager;

/**
 * Created by wangxingsheng on 2018/2/28.
 *
 */

public class DemoActivity extends AppCompatActivity{
    LockView<SimpleProcessManager> lockView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        lockView = findViewById(R.id.lockView);
        lockView.setProcessManager(new SimpleProcessManager());
        lockView.getProcessManager().setMode(SimpleProcessManager.SETTING_PASSWORD);
    }
}
