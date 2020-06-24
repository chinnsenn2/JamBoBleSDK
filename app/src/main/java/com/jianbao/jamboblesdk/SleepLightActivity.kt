package com.jianbao.jamboblesdk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class SleepLightActivity : AppCompatActivity() {
    private val mBtnOpenBle by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_open_ble) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep_light)

        mBtnOpenBle.setOnClickListener {
//            JamBoBleHelper.instance.scanSleepLightDevice()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}