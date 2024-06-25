package com.eason.stupidaccess.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.eason.stupidaccess.AccessApp;
import com.eason.stupidaccess.R;
import com.eason.stupidaccess.service.AccessibilityServiceMonitor;
import com.eason.stupidaccess.util.AccessibilitUtil;
import com.eason.stupidaccess.util.Config;
import com.eason.stupidaccess.util.LiveDataBus;
import com.eason.stupidaccess.util.ShareUtil;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TimePicker.OnTimeChangedListener {

    private ShareUtil mShareUtil;

    private TimePicker timepick;

    private Switch sw_keep;
    private Switch sw_liangtong;
    private Switch sw_alipay_forest;
    private Switch sw_wechart_motion;
    private Button btnSettings;
    private Button btnStart;

    private EditText etTableX;
    private EditText etTableY;

    private EditText etItemX;
    private EditText etItemY;
    private EditText etCardX;
    private EditText etCardY;
    private EditText etPwd;
    private EditText etDelay;

    public static int SERVICE_STATUS_ON = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        initView();
        initVaule();
        initListener();
//        startService();

        LiveDataBus.observeInt(Config.KEY_STATE, this, integer -> {
            if (integer == SERVICE_STATUS_ON) {
//                btnStart.setEnabled(false);
                btnStart.setText("已启动源计划");
            } else {
//                btnStart.setEnabled(true);
                btnStart.setText("启动源计划");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            updateUI();
        }
    }

    private void initView() {
        timepick = (TimePicker) findViewById(R.id.timepick);
//        sw_keep = (Switch) findViewById(R.id.sw_keep);
//        sw_liangtong = (Switch) findViewById(R.id.sw_liangtong);
        btnSettings = (Button) findViewById(R.id.btn_settings);
        sw_alipay_forest = (Switch) findViewById(R.id.sw_alipay_forest);
//        sw_wechart_motion = (Switch) findViewById(R.id.sw_wechart_motion);
        btnStart = (Button) findViewById(R.id.btn_start);
        etTableX = findViewById(R.id.et_table_x);
        etTableY = findViewById(R.id.et_table_y);
        etItemX = findViewById(R.id.et_item_x);
        etItemY = findViewById(R.id.et_item_y);
        etCardX = findViewById(R.id.et_card_x);
        etCardY = findViewById(R.id.et_card_y);
        etPwd = findViewById(R.id.et_pwd);
        etDelay = findViewById(R.id.et_delay);
    }

    private void initVaule() {
        mShareUtil = new ShareUtil(this);

        timepick.setIs24HourView(true);
        timepick.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        etTableX.setText(String.valueOf(mShareUtil.getInt(Config.KEY_TABLE_X, 0)));
        etTableY.setText(String.valueOf(mShareUtil.getInt(Config.KEY_TABLE_Y, 0)));
        etItemX.setText(String.valueOf(mShareUtil.getInt(Config.KEY_ITEM_X, 0)));
        etItemY.setText(String.valueOf(mShareUtil.getInt(Config.KEY_ITEM_Y, 0)));
        etCardX.setText(String.valueOf(mShareUtil.getInt(Config.KEY_CARD_X, 0)));
        etCardY.setText(String.valueOf(mShareUtil.getInt(Config.KEY_CARD_Y, 0)));
        etPwd.setText(mShareUtil.getString(Config.KEY_PWD, ""));
        etDelay.setText(String.valueOf(mShareUtil.getInt(Config.KEY_DELAY, 0)));
    }

    private void initListener() {
        btnSettings.setOnClickListener(this);
//        sw_keep.setOnCheckedChangeListener(this);
//        sw_liangtong.setOnCheckedChangeListener(this);
        sw_alipay_forest.setOnCheckedChangeListener(this);
//        sw_wechart_motion.setOnCheckedChangeListener(this);
        btnStart.setOnClickListener(this);

        timepick.setOnTimeChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_settings:
                AccessibilitUtil.showSettingsUI(this);
                break;
            case R.id.btn_start:
                if (!AccessibilitUtil.isAccessibilitySettingsOn(this, AccessibilityServiceMonitor.class.getCanonicalName())) {
                    Toast.makeText(this, "请先开启辅助功能，才能启动源计划", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etPwd.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请输入密码，才能启动源计划", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etTableX.getText().toString().isEmpty() || etTableY.getText().toString().isEmpty() ||
                        etItemX.getText().toString().isEmpty() || etItemY.getText().toString().isEmpty() ||
                        etCardX.getText().toString().isEmpty() || etCardY.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请输入坐标，才能启动源计划", Toast.LENGTH_SHORT).show();
                    return;
                }
                mShareUtil.setShare(Config.KEY_PWD, etPwd.getText().toString());
                mShareUtil.setShare(Config.KEY_ITEM_X, Integer.parseInt(etItemX.getText().toString()));
                mShareUtil.setShare(Config.KEY_ITEM_Y, Integer.parseInt(etItemY.getText().toString()));
                mShareUtil.setShare(Config.KEY_CARD_X, Integer.parseInt(etCardX.getText().toString()));
                mShareUtil.setShare(Config.KEY_CARD_Y, Integer.parseInt(etCardY.getText().toString()));
                mShareUtil.setShare(Config.KEY_DELAY, Integer.parseInt(etDelay.getText().toString()));
                mShareUtil.setShare(Config.KEY_TABLE_X, Integer.parseInt(etTableX.getText().toString()));
                mShareUtil.setShare(Config.KEY_TABLE_Y, Integer.parseInt(etTableY.getText().toString()));
                startService();
                Toast.makeText(this, "配置已保存", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateUI() {
        if (AccessibilitUtil.isAccessibilitySettingsOn(this, AccessibilityServiceMonitor.class.getCanonicalName())) {
            btnSettings.setEnabled(false);
        } else {
            btnSettings.setEnabled(true);
        }

//        sw_keep.setChecked(mShareUtil.getBoolean(Config.APP_KEEP, true));
        sw_alipay_forest.setChecked(mShareUtil.getBoolean(Config.APP_ALIPAY_FOREST, true));
//        sw_liangtong.setChecked(mShareUtil.getBoolean(Config.APP_LIANG_TONG, true));
//        sw_wechart_motion.setChecked(mShareUtil.getBoolean(Config.APP_WECHART_MOTHION, true));

        int hour = mShareUtil.getInt(Config.KEY_HOUR, -1);
        int minute = mShareUtil.getInt(Config.KEY_MINUTE, -1);

        if (hour == -1 && minute == -1) {
            // do nothing
        } else {
            timepick.setHour(hour);
            timepick.setMinute(minute);
        }
    }

    private void startService() {
        Intent mIntent = new Intent(this, AccessibilityServiceMonitor.class);
        startService(mIntent);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
//            case R.id.sw_keep:
//                mShareUtil.setShare(Config.APP_KEEP, b);
//                Log.d(Config.TAG, "Keep is " + b);
//                break;
            case R.id.sw_alipay_forest:
                mShareUtil.setShare(Config.APP_ALIPAY_FOREST, b);
                Log.d(Config.TAG, "AlipayForest is " + b);
                break;
//            case R.id.sw_liangtong:
//                mShareUtil.setShare(Config.APP_LIANG_TONG, b);
//                Log.d(Config.TAG, "LiangTong is " + b);
//                break;
//            case R.id.sw_wechart_motion:
//                mShareUtil.setShare(Config.APP_WECHART_MOTHION, b);
//                Log.d(Config.TAG, "Wechat mothion is " + b);
//                break;
        }

        Intent intent = new Intent(this, AccessibilityServiceMonitor.class);
        intent.setAction(AccessibilityServiceMonitor.ACTION_UPDATE_SWITCH);
        TaskActivity.this.startService(intent);
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        if (mShareUtil != null) {
            mShareUtil.setShare(Config.KEY_HOUR, hourOfDay);
            mShareUtil.setShare(Config.KEY_MINUTE, minute);

            AccessApp.Companion.startAlarmTask(TaskActivity.this);
        }
    }
}
