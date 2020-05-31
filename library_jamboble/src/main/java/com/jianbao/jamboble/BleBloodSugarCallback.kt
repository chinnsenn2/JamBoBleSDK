package com.jianbao.jamboble

import com.jianbao.jamboble.data.BloodSugarData

interface BleBloodSugarCallback : IBleDataCallback {
    fun onBTDataReceived(fatScaleData: BloodSugarData)
}