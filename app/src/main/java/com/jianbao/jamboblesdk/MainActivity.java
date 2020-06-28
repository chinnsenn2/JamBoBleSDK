package com.jianbao.jamboblesdk;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRvBle;

    private MeasureAdapter measureAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        mRvBle = findViewById(R.id.rv_ble);
        mRvBle.setLayoutManager(new LinearLayoutManager(this));
        measureAdapter = new MeasureAdapter();
        mRvBle.setAdapter(measureAdapter);
        List<MeasureBean> list = new ArrayList<>();
        list.add(new MeasureBean(0, "体脂测量"));
        list.add(new MeasureBean(1, "血压、血糖、尿酸测量"));
        list.add(new MeasureBean(2, "血氧测量"));
        list.add(new MeasureBean(3, "血液三合一测量"));
//        list.add(new MeasureBean(4, "睡眠灯"));
        list.add(new MeasureBean(5, "胎心"));
        measureAdapter.update(list);
        measureAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MeasureBean bean = (MeasureBean) adapter.getData().get(position);
                switch (bean.getType()) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, WeightActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, BloodActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, OxiMeterActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, BloodThreeOnOneActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, SleepLightActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this, FetalHeartActivity.class));
                        break;
                }
            }
        });
    }

    private int REQUEST_CODE_OPEN_GPS = 1;
    private int REQUEST_CODE_PERMISSION_LOCATION = 2;

    private void checkPermission() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
            return;
        }

        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (permissionDeniedList.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionDeniedList.toArray(new String[0]),
                    REQUEST_CODE_PERMISSION_LOCATION
            );
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage("当前手机扫描蓝牙需要打开定位功能。")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_OPEN_GPS);
                                        }
                                    }

                            )
                            .setCancelable(false)
                            .show();
                }
                break;
        }
    }

    private boolean checkGPSIsOpen() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
                //Todo
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static class MeasureBean {
        private int type;
        private String name;

        public MeasureBean(int type, String name) {
            this.type = type;
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class MeasureAdapter extends BaseQuickAdapter<MeasureBean, BaseViewHolder> {
        public MeasureAdapter() {
            super(R.layout.item_ble);
        }

        @Override
        protected void convert(BaseViewHolder helper, MeasureBean item) {
            helper.itemView.setBackgroundColor(randomColor());
            helper.setText(R.id.tv_name, item.getName());
        }

        public void update(List<MeasureBean> list) {
            this.getData().addAll(list);
            notifyDataSetChanged();
        }

        public int randomColor() {
            Random random = new Random();
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            return Color.argb(255, r, g, b);
        }
    }
}
