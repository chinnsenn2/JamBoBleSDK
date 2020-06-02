package com.jianbao.jamboble

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.qingniu.qnble.utils.QNLogUtils
import com.yolanda.health.qnblesdk.out.QNBleApi

class App : MultiDexApplication() {
    companion object {
        @JvmStatic
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        Logger.addLogAdapter(AndroidLogAdapter())
        initQnSDK()
    }

    private var mQnInitTime = 0

    private fun initQnSDK() {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            val encryptPath = "file:///android_asset/hzyb20160314175503.qn"
            QNBleApi.getInstance(this).also {
                QNLogUtils.setLogEnable(BuildConfig.DEBUG)
                it.initSdk("hzyb20160314175503", encryptPath) { code, msg ->
                    Log.d(
                        "BaseApplication", "code = [$code], msg = [$msg]"
                    )
                    if (code != 0) {
                        if (mQnInitTime < 3) {
                            mQnInitTime += 1
                            initQnSDK()
                        }
                    }
                }
            }

        }
    }
}