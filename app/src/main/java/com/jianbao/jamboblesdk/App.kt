package com.jianbao.jamboblesdk

import android.app.Application
import com.jianbao.fastble.JamBoBleHelper

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        JamBoBleHelper.instance.init(this)
    }

}