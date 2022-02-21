package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.example.myapplication.databinding.TitleBinding

class TitleLayout (context: Context, attrs: AttributeSet) : LinearLayout(context, attrs){
    private lateinit var binding: TitleBinding//ViewBinding 绑定需要加载的视图

//    private val binding: TitleBinding by lazy {
//        viewBinding(
//            TitleBinding::inflate,
//            true
//        )
//    }

    /**
     * 如何在 自定义View中 用ViewBing 绑定视图 ？
     * */

    init {
        LayoutInflater.from(context).inflate(R.layout.title, this)
        //这里的this 是给要加载的布局添加一个父布局

        //binding = inflate(LayoutInflater.from(context).inflate(R.layout.title, this))
        //val binding = RvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        //binding = TitleBinding.inflate(LayoutInflater.from(context))

//        val root = View.inflate(context, R.layout.title, this)
//        binding = TitleBinding.bind(root)

        val button: Button = findViewById(R.id.button_title_back)

        button.setOnClickListener {
            Toast.makeText(context,"点击了返回",Toast.LENGTH_LONG).show()
            Log.e("TAG", ">>>>>>> : click back button", )
            val activity = context as Activity
            activity.finish()
        }


    }



}