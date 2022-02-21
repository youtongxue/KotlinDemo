package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.myapplication.databinding.ActivityUiwidgetBinding

class UIWidgetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUiwidgetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uiwidget)

        binding = ActivityUiwidgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //按钮监听事件
        binding.buttonUiwidgetExcahngeImage.setOnClickListener {
            binding.imageViewUiwidget.setImageResource(R.drawable.androidstudio)
        }

        //ProgressBar   有问题，当控件为隐藏状态时，点击无效
        binding.progressBar.setOnClickListener {
            if (binding.progressBar.visibility == View.VISIBLE) {
                Log.i("UIWidget","控件显示")
                binding.progressBar.visibility = View.INVISIBLE
            } else {
                Log.i("UIWidget","控件隐藏")
                binding.progressBar.visibility = View.VISIBLE
            }
        }

        //动态改变进度
        binding.button5.setOnClickListener{
            if (binding.progressBarHorizontal.progress == 100){
                binding.progressBarHorizontal.progress = 0
            }else {
                binding.progressBarHorizontal.progress = binding.progressBarHorizontal.progress+10
            }

        };

    }
}