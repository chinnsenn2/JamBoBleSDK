package com.jianbao.jamboblesdk

import android.app.Application
import com.jianbao.fastble.JamBoHelper

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        JamBoHelper.getInstance().init(this)
        JamBoHelper.getInstance().enableDebug(true)
    }

}