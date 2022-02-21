package com.example.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.FruitRecyclerViewAdapter
import com.example.myapplication.bean.Fruit
import com.example.myapplication.databinding.ActivityRecyclerViewBinding

class RecyclerViewActivity : AppCompatActivity() {
    private val fruitList = ArrayList<Fruit>()
    private lateinit var binding: ActivityRecyclerViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        //bindingView
        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initFruits()
        val layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewDemo.layoutManager = layoutManager
        val adapter = FruitRecyclerViewAdapter(fruitList)
        binding.RecyclerViewDemo.adapter = adapter

    }

    //初始化水果数据
    private fun initFruits() {
        repeat(3) {
            fruitList.add(Fruit("Apple", R.drawable.ic_app))
            fruitList.add(Fruit("Watermelon", R.drawable.ic_watermelon))
            fruitList.add(Fruit("Orange", R.drawable.ic_orange))
            fruitList.add(Fruit("Banana", R.drawable.ic_banana))
            fruitList.add(Fruit("Grape", R.drawable.ic_grape))
            fruitList.add(Fruit("Pear", R.drawable.ic_pear))
        }
    }
}