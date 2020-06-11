package com.jianbao.jamboblesdk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val mBtnWeight by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_weight) }
    private val mBtnBloodPressure by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_blood_pressure) }
    private val mBtnThreeOnOne by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_threeOnOne) }
    private val mBtnBloodOx by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_blood_ox) }
    private val mBtnBloodFetalHeart by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_blood_fetal_heart) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnWeight.setOnClickListener(this)
        mBtnBloodPressure.setOnClickListener(this)
        mBtnThreeOnOne.setOnClickListener(this)
        mBtnBloodOx.setOnClickListener(this)
        mBtnBloodFetalHeart.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v) {
            mBtnWeight -> {
                startActivity(Intent(this, WeightActivity::class.java))
            }
            mBtnBloodPressure -> {
                startActivity(Intent(this, BloodActivity::class.java))
            }
            mBtnBloodOx -> {
                startActivity(Intent(this, OxiMeterActivity::class.java))
            }
            mBtnThreeOnOne -> {
                startActivity(Intent(this, BloodThreeOnOneActivity::class.java))
            }
            mBtnBloodFetalHeart -> {
                startActivity(Intent(this, FetalHeartActivity::class.java))
            }
        }
    }


}