package com.jianbao.jamboble

import com.jianbao.jamboble.data.BloodPressureData

interface BleBloodPressureCallback : IBleDataCallback {
    fun onBTDataReceived(fatScaleData: BloodPressureData)
}