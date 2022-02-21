package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // javaClass.simpleName >>> get bean and bean name
        Log.e("BaseActivity", javaClass.simpleName)

        //used ActivityCollector to collector activity
        ActivityCollector.addActivity(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }
}