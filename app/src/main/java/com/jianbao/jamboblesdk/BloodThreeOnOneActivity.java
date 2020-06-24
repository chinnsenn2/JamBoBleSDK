package com.jianbao.jamboblesdk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jianbao.fastble.JamBoHelper;
import com.jianbao.jamboble.BleState;
import com.jianbao.jamboble.callbacks.BleDataCallback;
import com.jianbao.jamboble.data.BTData;
import com.jianbao.jamboble.data.BloodPressureData;
import com.jianbao.jamboble.data.BloodSugarData;
import com.jianbao.jamboble.data.CholestenoneData;

import org.jetbrains.annotations.NotNull;

public class BloodThreeOnOneActivity extends AppCompatActivity {

    private TextView mTvStatus;
    private TextView mTvValue;
    private Button mBtnOpenBle;
    private TextView mTvDataTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_three_on_one);

        mTvStatus = findViewById(R.id.tv_status);
        mTvValue = findViewById(R.id.tv_value);
        mBtnOpenBle = findViewById(R.id.btn_open_ble);
        mTvDataTitle = findViewById(R.id.tv_data_title);

        setTitle("血液三项合一测量");

        JamBoHelper.getInstance().setBleDataCallBack(new BleDataCallback() {
            @Override
            public void onBTStateChanged(@NotNull BleState state) {
                switch (state) {
                    //未开启蓝牙
                    case NOT_FOUND:
                        mTvStatus.setText("请打开蓝牙");
                        break;
                    //正在扫描
                    case SCAN_START:
                        mBtnOpenBle.setText("停止扫描");
                        mTvStatus.setText("开始扫描...");
                        break;
                    //连接成功
                    case CONNECTED:
                        mTvStatus.setText("连接设备成功");
                        break;
                    //长时间未搜索到设备
                    case TIMEOUT:
                        mBtnOpenBle.setText("开始扫描");
                        mTvStatus.setText("超时");
                        break;
                    case CONNECTEING:
                        mBtnOpenBle.setText("开始扫描");
                        mTvStatus.setText("连接中");
                        break;
                    case DISCONNECT:
                        mTvStatus.setText("断开连接");
                        break;
                    case CONNECT_FAILED:
                        mTvStatus.setText("连接失败");
                        break;
                    case SCAN_STOP:
                        mBtnOpenBle.setText("开始扫描");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onBTDataReceived(@Nullable BTData data) {
                if (data instanceof BloodPressureData) {
                    mTvDataTitle.setText("血压数据结果");
                } else if (data instanceof BloodSugarData) {
                    mTvDataTitle.setText("血糖数据结果");
                } else if (data instanceof CholestenoneData) {
                    mTvDataTitle.setText("胆固醇数据结果");
                }
                mTvValue.setText(data.toString());
            }

            @Override
            public void onLocalBTEnabled(boolean enabled) {

            }
        });

        mBtnOpenBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JamBoHelper.getInstance().scanThreeOnOneDevice();
            }
        });
    }

    @Override
    public void onDestroy() {
        //释放资源
        JamBoHelper.getInstance().destroy();
        super.onDestroy();
    }
}
