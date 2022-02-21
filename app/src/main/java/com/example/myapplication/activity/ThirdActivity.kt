package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.myapplication.ActivityCollector
import com.example.myapplication.BaseActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityThirdBinding

class ThirdActivity : BaseActivity() {

    private lateinit var binding: ActivityThirdBinding

    //3.6.3启动Activity的最佳写法
    companion object {
        fun actionStart(context: Context, data: String) {
            val intent = Intent(context, ThirdActivity::class.java)
            intent.putExtra("param", data)
            context.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonThirdActivityBack.setOnClickListener { finish() }
        binding.buttonThirdActivityFinishAll.setOnClickListener {
            ActivityCollector.finishAll()
        }

        binding.actionStart.text = intent.getStringExtra("param")

    }


}