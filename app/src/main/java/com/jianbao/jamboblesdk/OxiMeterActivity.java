package com.jianbao.jamboblesdk;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creative.FingerOximeter.FingerOximeter;
import com.creative.FingerOximeter.IFingerOximeterCallBack;
import com.creative.base.BaseDate;
import com.jianbao.fastble.JamBoHelper;
import com.jianbao.fastble.data.BleDevice;
import com.jianbao.jamboble.BleState;
import com.jianbao.jamboble.callbacks.BleDataCallback;
import com.jianbao.jamboble.callbacks.IBleStatusCallback;
import com.jianbao.jamboble.data.BTData;
import com.jianbao.jamboble.data.OximeterData;
import com.jianbao.jamboble.data.SpO2Data;
import com.jianbao.jamboble.device.BTDevice;
import com.jianbao.jamboble.device.oximeter.OxiMeterHelper;
import com.jianbao.jamboble.device.oximeter.OximeterDevice;
import com.jianbao.jamboble.device.oximeter.OximeterReader;
import com.jianbao.jamboble.device.oximeter.OximeterWriter;
import com.jianbao.jamboble.draw.DrawThreadNW;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.jianbao.jamboble.device.oximeter.FingerOximeterCallback.MSG_DATA_SPO2_PARA;
import static com.jianbao.jamboble.device.oximeter.FingerOximeterCallback.MSG_DATA_SPO2_WAVE;
import static com.jianbao.jamboble.device.oximeter.FingerOximeterCallback.MSG_PROBE_OFF;

public class OxiMeterActivity extends AppCompatActivity {
    private DrawThreadNW mDtBloodOx;
    private LinearLayout mLayoutMeasuringGuide;
    private TextView mTvValueRealtime;
    private Button mBtnOpenBle;
    private TextView mTvStatus;

    private FingerOximeter fingerOximeter;
    private OximeterWriter oximeterWriter;
    private OximeterReader oximeterReader;

    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oxi_meter);

        setTitle("????????????");

        mDtBloodOx = findViewById(R.id.dt_blood_ox);
        mLayoutMeasuringGuide = findViewById(R.id.layout_measuring_guide);
        mTvValueRealtime = findViewById(R.id.tv_value_realtime);
        mBtnOpenBle = findViewById(R.id.btn_open_ble);
        mTvStatus = findViewById(R.id.tv_status);

        JamBoHelper.getInstance().setBleDataCallBack(new BleDataCallback() {
            @Override
            public void onBTStateChanged(@NotNull BleState state) {
                switch (state) {
                    //???????????????
                    case NOT_FOUND:
                        mTvStatus.setText("???????????????");
                        break;
                    //????????????
                    case SCAN_START:
                        mBtnOpenBle.setText("????????????");
                        mTvStatus.setText("????????????...");
                        break;
                        //????????????
                    case CONNECTED:
                        mTvStatus.setText("??????????????????");
                        mLayoutMeasuringGuide.setVisibility(View.INVISIBLE);
                        break;
                    //???????????????????????????
                    case TIMEOUT:
                        mBtnOpenBle.setText("????????????");
                        mTvStatus.setText("??????");
                        break;
                    case CONNECTING:
                        mBtnOpenBle.setText("????????????");
                        mTvStatus.setText("?????????");
                        break;
                    case DISCONNECT:
                        mDtBloodOx.cleanWaveData();
                        pauseRecord();
                        if (oximeterReader != null) {
                            oximeterReader.close();
                        }
                        if (oximeterWriter != null) {
                            oximeterWriter.close();
                        }
                        JamBoHelper.getInstance().onBTStateChanged(BleState.SCAN_START);
                        mTvStatus.setText("????????????");
                        break;
                    case CONNECT_FAILED:
                        mTvStatus.setText("????????????");
                        break;
                    case SCAN_STOP:
                        mBtnOpenBle.setText("????????????");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onBTDataReceived(@Nullable BTData data) {
                if (data instanceof OximeterData) {
                    BTDevice device = JamBoHelper.getInstance().getBtDevice();
                    if (device instanceof OximeterDevice) {
                        ((OximeterDevice) device).getOximeterHelper().addBuffer(((OximeterData) data).getData());
                    }
                }
            }

            @Override
            public void onLocalBTEnabled(boolean enabled) {

            }
        });

        JamBoHelper.getInstance().setBleStatusCallback(new IBleStatusCallback() {
            @Override
            public void onBTDeviceFound(List<BleDevice> list) {

            }

            @Override
            public void onNotification() {
                BTDevice device = JamBoHelper.getInstance().getBtDevice();
                if (device instanceof OximeterDevice) {
                    OxiMeterHelper helper = ((OximeterDevice) device).getOximeterHelper();
                    oximeterReader = new OximeterReader(helper);
                    oximeterWriter = new OximeterWriter(helper);
                    fingerOximeter = new FingerOximeter(oximeterReader, oximeterWriter,
                            new IFingerOximeterCallBack(){

                                public void OnGetSpO2Param(int nSpO2, int nPR, float fPI, boolean nStatus, int nMode, float nPower, int powerLevel) {
                                    SpO2Data mSpO2Data = new SpO2Data();
                                    mSpO2Data.setSpO2(nSpO2);
                                    mSpO2Data.setPR(nPR);
                                    mSpO2Data.setPI(fPI);
                                    mSpO2Data.setStatus(nStatus);
                                    mSpO2Data.setMode(nMode);
                                    mSpO2Data.setPower(nPower);
                                }

                                @Override
                                public void OnGetSpO2Wave(List<BaseDate.Wave> list) {

                                }

                                @Override
                                public void OnGetDeviceVer(String s, String s1, String s2) {

                                }

                                @Override
                                public void OnConnectLose() {

                                }
                            });
                    fingerOximeter.Start();
                    fingerOximeter.SetWaveAction(true);
                }
            }
        });

        handler = new CurveHandler(this);
        mDtBloodOx.setmHandler(handler);

        mBtnOpenBle.setOnClickListener(v -> {
            switch (mBtnOpenBle.getText().toString()) {
                case "????????????":
                    JamBoHelper.getInstance().scanOxiMeterDevice();
                    break;
                case "????????????":
                    JamBoHelper.getInstance().stopScan();
                    break;
            }
        });

    }

    /**
     * ????????????????????????????????????
     */
    private void pauseRecord() {
        if (mDtBloodOx != null) {
            mDtBloodOx.pauseDraw();
        }
        if (fingerOximeter != null) {
            fingerOximeter.Stop();
        }
    }

    private static class CurveHandler extends Handler{
        private WeakReference<OxiMeterActivity> weakReference;

        public CurveHandler(OxiMeterActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            OxiMeterActivity activity = weakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MSG_DATA_SPO2_WAVE:
                        //??????????????????????????????
                        activity.mDtBloodOx.startDraw();
                        break;
                    case MSG_DATA_SPO2_PARA:
                        //????????????
                        if (msg.getData() != null) {
                            SpO2Data spO2Data = (SpO2Data) msg.getData().getSerializable("data");
                            if (spO2Data.getSpO2() > 0 && spO2Data.getPR() > 0) {
                                if (!spO2Data.isStatus()) {
                                    sendEmptyMessage(MSG_PROBE_OFF);
                                } else {
                                    activity.mTvValueRealtime.setText(spO2Data.toString());
                                }
                            }
                        }
                        break;
                    case MSG_PROBE_OFF:
                        activity.mDtBloodOx.cleanWaveData();
                        activity.pauseRecord();
                        if (activity.oximeterReader != null) {
                            activity.oximeterReader.close();
                        }
                        if (activity.oximeterWriter != null) {
                            activity.oximeterWriter.close();
                        }
                        JamBoHelper.getInstance().onBTStateChanged(BleState.SCAN_START);
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        mDtBloodOx.cleanWaveData();
        pauseRecord();
        if (oximeterReader != null) {
            oximeterReader.close();
        }
        if (oximeterWriter != null) {
            oximeterWriter.close();
        }
        JamBoHelper.getInstance().destroy();
        super.onDestroy();
    }
}
