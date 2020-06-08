package com.jianbao.jamboble.callbacks

import com.jianbao.jamboble.BleState
import com.jianbao.jamboble.data.BTData

interface BleDataCallback {
    fun onBTStateChanged(state: BleState)
    fun onBTDataReceived(data: BTData?)
}