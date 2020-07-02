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
import com.jianbao.jamboble.data.UricAcidData;

import org.jetbrains.annotations.NotNull;

public class BloodActivity extends AppCompatActivity {

    private TextView mTvStatus;
    private TextView mTvValue;
    private Button mBtnOpenBle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood);
        mTvStatus = findViewById(R.id.tv_status);
        mTvValue = findViewById(R.id.tv_value);
        mBtnOpenBle = findViewById(R.id.btn_open_ble);
//        setTitle("血压测量");
        setTitle("血糖测量");
//        setTitle("尿酸测量");

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
                    case CONNECTING:
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
                     /*
                        //收缩压
                        public int systolicPressur
                        //舒张压
                        public int diastolicPressu
                        //心率
                        public int heartRate;
                         */
                    mTvValue.setText(data.toString());
                }
                //血糖
                if (data instanceof BloodSugarData) {
                //data.bloodSugar 血糖值 单位 Mmol
                    mTvValue.setText(data.toString());
                }
                //尿酸
                if (data instanceof UricAcidData) {
                //data.mUricAcid 尿酸值 单位mmol/L
                    mTvValue.setText(data.toString());
                }
            }

            @Override
            public void onLocalBTEnabled(boolean enabled) {

            }
        });

        mBtnOpenBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                JamBoHelper.getInstance().scanBloodPressureDevice();
                JamBoHelper.getInstance().scanBloodSugarDevice();
//                JamBoHelper.getInstance().scanUricAcidDevice();
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
