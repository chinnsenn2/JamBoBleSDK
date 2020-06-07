package com.jianbao.jamboble.callbacks

import com.jianbao.jamboble.data.BTData

interface BleDataCallback {
    fun onBTStateChanged(state: Int)
    fun onBTDataReceived(data: BTData?)
}