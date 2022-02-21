package com.example.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.adapter.FruitListViewAdapter
import com.example.myapplication.bean.Fruit
import com.example.myapplication.databinding.ActivityListViewBinding

class ListViewActivity : AppCompatActivity() {
    private val fruitList = ArrayList<Fruit>()//实例化水果集合
    private lateinit var binding: ActivityListViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        binding = ActivityListViewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //初始化水果数据
        initFruits()
        val adapter = FruitListViewAdapter(this, R.layout.listview_demo_item, fruitList)
        binding.listviewDemo.adapter = adapter
        //单个item的点击事件
        binding.listviewDemo.setOnItemClickListener { parent, view, position, id ->
            val fruit = fruitList[position]//更具 点击的位置 得到列表里面的水果名称
            Toast.makeText(this, "点击了 ${fruit.name}", Toast.LENGTH_SHORT).show()
        }

    }
    //初始化水果数据
    private fun initFruits() {
        repeat(2) {
            fruitList.add(Fruit("Apple", R.drawable.ic_app))
            fruitList.add(Fruit("Banana", R.drawable.ic_banana))
            fruitList.add(Fruit("Orange", R.drawable.ic_orange))
            fruitList.add(Fruit("Watermelon", R.drawable.ic_watermelon))
            fruitList.add(Fruit("Pear", R.drawable.ic_pear))
            fruitList.add(Fruit("Grape", R.drawable.ic_grape))
        }
    }

}