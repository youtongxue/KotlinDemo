package com.example.myapplication.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.adapter.FruitRecyclerViewHorizontalAdapter
import com.example.myapplication.adapter.MyViewPagerAdapter
import com.example.myapplication.bean.Fruit
import com.example.myapplication.databinding.ActivityRecyclerViewHorizontalBinding
import com.example.myapplication.util.Utils


class RecyclerViewHorizontalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecyclerViewHorizontalBinding
    private val fruitList = ArrayList<Fruit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view_horizontal)

        binding = ActivityRecyclerViewHorizontalBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        initEmoji()
        initFruits()
        //设置 RecyclerView的 布局方式（方向）
        val layManager = LinearLayoutManager(this)//实例化 LayoutManager
        layManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerviewHoriztonal.layoutManager = layManager
        //将数据传入Adapter
        val adapter = FruitRecyclerViewHorizontalAdapter(fruitList)
        binding.recyclerviewHoriztonal.adapter = adapter


        binding.ViewPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                Utils.utilToast(this@RecyclerViewHorizontalActivity, "滑动到 $position 页")


            }
        })

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

    //初始化表情布局视图
    fun initEmoji() {
        val lf = LayoutInflater.from(this)
        val view1 = lf.inflate(R.layout.emoji_one, null)
        val view2 = lf.inflate(R.layout.emoji_two, null)

        val viewList = ArrayList<View>() // 将要分页显示的View装入数组中

        viewList.add(view1)
        viewList.add(view2)
        //viewList.add(view3)

        binding.ViewPage.adapter = MyViewPagerAdapter(viewList)
        binding.ViewPage.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        //viewPager.setOnPageChangeListener(new MyOnPageChangeListener());//设置页面切换时候的监听器(可选，用了之后要重写它的回调方法处理页面切换时候的事务)

    }


}