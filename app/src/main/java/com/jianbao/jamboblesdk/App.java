package com.jianbao.jamboblesdk;

import android.app.Application;

import com.jianbao.fastble.JamBoHelper;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JamBoHelper.getInstance().init(this);
        JamBoHelper.getInstance().enableDebug(true);
    }
}
