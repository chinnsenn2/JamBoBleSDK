package com.jianbao.jamboblesdk

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.util.*

class MainActivity : AppCompatActivity() {
    private val mRvBle by lazy(LazyThreadSafetyMode.NONE) { findViewById<RecyclerView>(R.id.rv_ble) }
    private val mAdapter = MeasureAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()

        mRvBle.also {
            it.layoutManager = LinearLayoutManager(this@MainActivity)
            it.adapter = mAdapter
        }
        mAdapter.setOnItemClickListener { adapter, view, position ->
            when (position) {
                0 -> {
                    startActivity(Intent(this, Weight2Activity::class.java))
                }
                1 -> {
                    startActivity(Intent(this, BloodActivity::class.java))
                }
                2 -> {
                    startActivity(Intent(this, OxiMeterActivity::class.java))
                }
                3 -> {
                    startActivity(Intent(this, BloodThreeOnOneActivity::class.java))
                }
                4 -> {
                    startActivity(Intent(this, SleepLightActivity::class.java))
                }
                5 -> {
                    startActivity(Intent(this, FetalHeartActivity::class.java))
                }
            }
        }

        val list = mutableListOf<MeasureBean>()
        list.add(MeasureBean(0, "体重测量"))
        list.add(MeasureBean(1, "血压、血糖、尿酸测量"))
        list.add(MeasureBean(3, "血氧测量"))
        list.add(MeasureBean(2, "血液三合一测量"))
        list.add(MeasureBean(4, "睡眠灯"))
        list.add(MeasureBean(5, "胎心"))
        mAdapter.update(list)
    }

    data class MeasureBean(var type: Int, var name: String)

    class MeasureAdapter : BaseQuickAdapter<MeasureBean, BaseViewHolder>(R.layout.item_ble) {
        override fun convert(helper: BaseViewHolder?, item: MeasureBean?) {
            helper?.itemView?.setBackgroundColor(randomColor())
            helper?.setText(R.id.tv_name, item?.name)
        }

        fun update(list: List<MeasureBean>) {
            this.data.addAll(list)
            notifyDataSetChanged()
        }

        fun randomColor(): Int {
            val random = Random()
            val r: Int = random.nextInt(256)
            val g: Int = random.nextInt(256)
            val b: Int = random.nextInt(256)
            return Color.argb(255, r, g, b)
        }
    }

    private val REQUEST_CODE_OPEN_GPS = 1
    private val REQUEST_CODE_PERMISSION_LOCATION = 2


    private fun checkPermissions() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter.isEnabled) {
            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).also {
                startActivityForResult(it, 1)
            }
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