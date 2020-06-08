package com.jianbao.jamboblesdk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val mBtnWeight by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_weight) }
    private val mBtnBloodPressure by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_blood_pressure) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnWeight.setOnClickListener(this)
        mBtnBloodPressure.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v) {
            mBtnWeight -> {
                startActivity(Intent(this, WeightActivity::class.java))
            }
            mBtnBloodPressure -> {
                startActivity(Intent(this, BloodActivity::class.java))
            }
        }
    }


}