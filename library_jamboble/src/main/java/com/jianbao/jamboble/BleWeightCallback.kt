package com.jianbao.jamboble

import com.jianbao.jamboble.data.FatScaleData

interface BleWeightCallback : IBleDataCallback {

    fun onBTDataReceived(fatScaleData: FatScaleData)

    fun onUnsteadyValue(value: Float)
}