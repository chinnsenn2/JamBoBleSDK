package com.medica.xiangshui.jni;


import com.medica.xiangshui.jni.phone.PhoneAlgorithmIn;
import com.medica.xiangshui.jni.phone.PhoneAlgorithmOut;

/**
 * Created by Administrator on 2017/11/10.
 */

public class AlgorithmUtils {

    static {
        System.loadLibrary("algorithm");
    }

/****************************************************单手机算法开始****************************************************/
    /**
     * 单手机助眠算法
     *
     * @return
     */
    public static native PhoneAlgorithmOut phone(PhoneAlgorithmIn in);

/****************************************************单手机算法结束****************************************************/
}

//    No implementation found for com.android.medica.phone.bean.PhoneAlgorithmOut com.android.medica.phone.utils.AlgorithmUtils.phone(com.android.medica.phone.bean.PhoneAlgorithmIn) (tried Java_com_android_medica_phone_utils_AlgorithmUtils_phone and Java_com_android_medica_phone_utils_AlgorithmUtils_phone__Lcom_android_medica_phone_bean_PhoneAlgorithmIn_2)
