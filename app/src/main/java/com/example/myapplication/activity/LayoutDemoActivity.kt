package com.example.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLayoutDemoBinding

class LayoutDemoActivity : AppCompatActivity() {
    //视图绑定
    private lateinit var binding: ActivityLayoutDemoBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_demo)

        //隐藏 ActionBar
        supportActionBar?.hide()

        binding = ActivityLayoutDemoBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

    }
}