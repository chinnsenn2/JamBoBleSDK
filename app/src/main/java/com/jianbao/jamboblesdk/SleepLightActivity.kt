package com.jianbao.jamboblesdk

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.jianbao.fastble.JamBoBleHelper

class SleepLightActivity : AppCompatActivity() {
    private val mBtnOpenBle by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_open_ble) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep_light)

        mBtnOpenBle.setOnClickListener {
            JamBoBleHelper.instance.scanSleepLightDevice()
        }
    }


    override fun onDestroy() {
        JamBoBleHelper.instance.scanSleepLightDevice()
        super.onDestroy()
    }

}