package com.jianbao.jamboble.callbacks

interface BleDataCallback {
    fun onBTStateChanged(state: Int)
    fun onBTDataReceived(data: BTData?)
}