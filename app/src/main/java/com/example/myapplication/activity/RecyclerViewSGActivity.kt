package com.example.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.FruitListViewSgAdapter
import com.example.myapplication.bean.Fruit
import com.example.myapplication.databinding.ActivityRecyclerViewSgactivityBinding
import java.lang.StringBuilder

class RecyclerViewSGActivity : AppCompatActivity() {
    private val fruitList = ArrayList<Fruit>()
    private lateinit var binding: ActivityRecyclerViewSgactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view_sgactivity)

        //bindingView
        binding = ActivityRecyclerViewSgactivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initFruits()
        //将布局样式设置为瀑布流样式，垂直排3列
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerSg.layoutManager = layoutManager
        val adapter = FruitListViewSgAdapter(fruitList)
        binding.recyclerSg.adapter = adapter

    }

    //初始化水果数据
    private fun initFruits() {
        repeat(3) {
            fruitList.add(Fruit(getRandomLengthString("Apple"), R.drawable.ic_app))
            fruitList.add(Fruit(getRandomLengthString("Watermelon"), R.drawable.ic_watermelon))
            fruitList.add(Fruit(getRandomLengthString("Orange"), R.drawable.ic_orange))
            fruitList.add(Fruit(getRandomLengthString("Banana"), R.drawable.ic_banana))
            fruitList.add(Fruit(getRandomLengthString("Grape"), R.drawable.ic_grape))
            fruitList.add(Fruit(getRandomLengthString("Pear"), R.drawable.ic_pear))
        }
    }

    //将字符串随机循环，叠加
    private fun getRandomLengthString(str: String): String {
        val n = (1..20).random()
        val builder = StringBuilder()
        repeat(n) {
            builder.append(str)
        }
        return builder.toString()
    }
}