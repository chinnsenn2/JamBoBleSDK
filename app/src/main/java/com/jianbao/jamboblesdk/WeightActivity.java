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
import com.jianbao.jamboble.callbacks.UnSteadyValueCallBack;
import com.jianbao.jamboble.data.BTData;
import com.jianbao.jamboble.data.FatScaleData;
import com.jianbao.jamboble.data.QnUser;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class WeightActivity extends AppCompatActivity {
    private TextView mTvStatus;
    private TextView mTvValueRealtime;
    private TextView mTvValue;
    private Button mBtnOpenBle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);
        setTitle("体脂测量");
        mTvStatus = findViewById(R.id.tv_status);
        mTvValueRealtime = findViewById(R.id.tv_value_realtime);
        mTvValue = findViewById(R.id.tv_value);
        mBtnOpenBle = findViewById(R.id.btn_open_ble);

        mBtnOpenBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mBtnOpenBle.getText().toString()) {
                    case "开始扫描":
                        JamBoHelper.getInstance().scanFatScaleDevice();
                        break;
                    case "停止扫描":
                        JamBoHelper.getInstance().stopScan();
                        break;
                }
            }
        });

        JamBoHelper.getInstance().updateQnUser(new QnUser("1", "male", 180, new Date()));

        JamBoHelper.getInstance().setUnSteadyValueCallBack(new UnSteadyValueCallBack() {
            @Override
            public void onUnsteadyValue(float value) {
                mTvValueRealtime.setText(value + "kg");
            }
        });

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
                    case SCAN_STOP:
                        mBtnOpenBle.setText("开始扫描");
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
                    default:
                        break;
                }
            }


            @Override
            public void onBTDataReceived(@Nullable BTData data) {
                if (data instanceof FatScaleData) {
                    mTvValue.setText(data.toString());
                }
                /*
                    //体重
                    public float weight;
                    //脂肪
                    public float fat;
                    //水分
                    public float tbw;
                    //BMI
                    public float bmi;
                    //皮下脂肪率
                    public float subcutaneousfat;
                    //内脏脂肪等级
                    public float viscerallevel;
                    //骨骼肌率
                    public float skeletal;
                    //骨量
                    public float bonemass;
                    //蛋白质含量
                    public float proteins;
                    //基础代谢
                    public float metabolic;
                    //体年龄
                    public float bodyage;
                    //分数
                    public float score;
                    //体型
                    public String bodyshape;
                     */
            }

            @Override
            public void onLocalBTEnabled(boolean enabled) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        JamBoHelper.getInstance().destroy();
        super.onDestroy();
    }
}
