package com.jianbao.jamboblesdk

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val mBtnWeight by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_weight) }
    private val mBtnBloodPressure by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_blood_pressure) }
    private val mBtnThreeOnOne by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_threeOnOne) }
    private val mBtnBloodOx by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_blood_ox) }
    private val mBtnBloodFetalHeart by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_blood_fetal_heart) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnWeight.setOnClickListener(this)
        mBtnBloodPressure.setOnClickListener(this)
        mBtnThreeOnOne.setOnClickListener(this)
        mBtnBloodOx.setOnClickListener(this)
        mBtnBloodFetalHeart.setOnClickListener(this)

        checkPermissions()
    }

    override fun onClick(v: View?) {
        when (v) {
            mBtnWeight -> {
                startActivity(Intent(this, Weight2Activity::class.java))
            }
            mBtnBloodPressure -> {
                startActivity(Intent(this, BloodActivity::class.java))
            }
            mBtnBloodOx -> {
                startActivity(Intent(this, OxiMeterActivity::class.java))
            }
            mBtnThreeOnOne -> {
                startActivity(Intent(this, BloodThreeOnOneActivity::class.java))
            }
            mBtnBloodFetalHeart -> {
                startActivity(Intent(this, FetalHeartActivity::class.java))
            }
        }
    }

    private val REQUEST_CODE_OPEN_GPS = 1
    private val REQUEST_CODE_PERMISSION_LOCATION = 2

    private fun checkPermissions() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "请先打开蓝牙", Toast.LENGTH_LONG).show()
            return
        }
        val permissions =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val permissionDeniedList: MutableList<String> =
            ArrayList()
        for (permission in permissions) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, permission)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission)
            } else {
                permissionDeniedList.add(permission)
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            val deniedPermissions =
                permissionDeniedList.toTypedArray()
            ActivityCompat.requestPermissions(
                this,
                deniedPermissions,
                REQUEST_CODE_PERMISSION_LOCATION
            )
        }
    }

    private fun onPermissionGranted(permission: String) {
        when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("当前手机扫描蓝牙需要打开定位功能。")
                    .setNegativeButton(
                        "取消"
                    ) { _, _ -> finish() }
                    .setPositiveButton(
                        "前往设置"
                    ) { _, _ ->
                        val intent =
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivityForResult(intent, REQUEST_CODE_OPEN_GPS)
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }

    private fun checkGPSIsOpen(): Boolean {
        val locationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                ?: return false
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
                //TODO
            }
        }
    }
}