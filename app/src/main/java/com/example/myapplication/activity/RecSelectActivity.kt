package com.example.myapplication.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.adapter.MyViewPagerAdapter
import com.example.myapplication.adapter.RecAdapter
import com.example.myapplication.util.Utils

class RecSelectActivity : AppCompatActivity() {
    var adapter: RecAdapter? = null
    var list: MutableList<String> = ArrayList()
    var temp = 0 //记录上次选中位置

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rec_select)

        val recyclerView: RecyclerView = findViewById(R.id.recy)
        val ViewPager :ViewPager2 = findViewById(R.id.EmojiViewPage)

        for (i in 0..6) {
            list.add("item")
        }


        //设置 RecyclerView的 布局方式（方向）
        val layManager = LinearLayoutManager(this)//实例化 LayoutManager
        layManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = layManager
        val adapter = RecAdapter(this, R.layout.rec_item, list)
        recyclerView.setAdapter(adapter)

        //RecyclerView Item刷新数据动画
        //(recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false//取消切换动画
        (recyclerView.itemAnimator as DefaultItemAnimator).removeDuration = 2

        //RecyclerView Item点击监听
        adapter.setOnMyItemClickListener(object : RecAdapter.OnMyItemClickListener{
            override fun myClick(pos: Int) {
                Utils.utilToast(this@RecSelectActivity,"点击 ： $pos")
                ViewPager.currentItem = pos//ViewPager滑动到指定页面， 在 ViewPager 的滑动监听回调方法 OnPageChangeCallback -> 做RecyclerView Item修改背景操作
                recyclerView.smoothScrollToPosition(pos)//RecyclerView滑动到选择项
                temp = pos//设置当前选中页，在refreshBg方法中 更新单个Item -> notifyItemChange() 需要，若 全部更新 -> notifyDataSetChanged() 则不需要设置

            }

        })

        //初始化表情布局视图
            val lf = LayoutInflater.from(this)
            val view1 = lf.inflate(R.layout.emoji_one, null)
            val view2 = lf.inflate(R.layout.emoji_two, null)
            val viewList = ArrayList<View>() // 将要分页显示的View装入数组中
            viewList.add(view1)
            viewList.add(view2)

            ViewPager.adapter = MyViewPagerAdapter(viewList)
            ViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        //ViewPager 滑动监听回调
        ViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                    Log.e(" >>    >>", "被调用")
                    Utils.utilToast(this@RecSelectActivity, "滑动到 $position 页")
                    adapter.refreshBg(position, temp)//向RecyclerView Adapter传入当前ViewPager选中页位置
                    temp = position
                    recyclerView.smoothScrollToPosition(position)
            }

            //删除了另两个方法 override fun onPageScrollStateChanged , override fun onPageScrolled
        })

    }
}