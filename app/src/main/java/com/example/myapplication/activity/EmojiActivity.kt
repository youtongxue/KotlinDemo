package com.example.myapplication.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.adapter.EmojiMiniAdapter
import com.example.myapplication.databinding.ActivityEmojiBinding
import com.example.myapplication.fragment.EmojiOneFragment
import com.example.myapplication.fragment.EmojiTwoFragment
import com.example.myapplication.myinterface.EmojiInterFace
import com.example.myapplication.util.Utils


class EmojiActivity : AppCompatActivity(), EmojiInterFace {
    private lateinit var binding: ActivityEmojiBinding
    var emojiMiniList: MutableList<Drawable> = ArrayList() //EmojiMini 图片
    var temp = 0 //记录上次选中位置

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emoji)

        //bindingView
        binding = ActivityEmojiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initEmojiMini()//初始化底部 emoji 预览数据
        initFragment()//初始化，表情选择的 fragment 页面
        choseEmoji()


    }

    //初始化底部 emoji 预览数据
    fun  initEmojiMini() {
        //设置 EmojiMiniRecyclerView的 布局方式（方向）
        val layManager = LinearLayoutManager(this)//实例化 LayoutManager
        layManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.emojiminiRcy.layoutManager = layManager

        //设置adapter
        emojiMiniList.add(getDrawable(R.drawable.ic_app)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_orange)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_grape)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_pear)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_watermelon)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_banana)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_app)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_orange)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_grape)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_pear)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_watermelon)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_banana)!!)
        val adapter = EmojiMiniAdapter(this, emojiMiniList)
        binding.emojiminiRcy.setAdapter(adapter)

        //RecyclerView Item刷新数据动画
        //(recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false//取消切换动画
        (binding.emojiminiRcy.itemAnimator as DefaultItemAnimator).removeDuration = 2

        //底部 emoji 预览 RecyclerView Item点击监听
        adapter.setOnMyItemClickListener(object : EmojiMiniAdapter.OnMyItemClickListener{
            override fun myClick(pos: Int) {
                Utils.utilToast(this@EmojiActivity,"点击 ： $pos")
                binding.emojiViewpager.currentItem = pos//ViewPager滑动到指定页面， 在 ViewPager 的滑动监听回调方法 OnPageChangeCallback -> 做RecyclerView Item修改背景操作
                binding.emojiminiRcy.smoothScrollToPosition(pos+1)//RecyclerView滑动到选择项
                temp = pos//设置当前选中页，在refreshBg方法中 更新单个Item -> notifyItemChange() 需要，若 全部更新 -> notifyDataSetChanged() 则不需要设置

            }

        })

        //ViewPager 滑动监听回调
        binding.emojiViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.e(" >>    >>", "被调用")
                Utils.utilToast(this@EmojiActivity, "滑动到 $position 页")
                adapter.refreshBg(position, temp)//向RecyclerView Adapter传入当前ViewPager选中页位置
                temp = position
                binding.emojiminiRcy.smoothScrollToPosition(position+1)
            }

            //删除了另两个方法 override fun onPageScrollStateChanged , override fun onPageScrolled
        })
    }

    //初始化，表情选择的 fragment 页面
    fun initFragment() {
        val list: MutableList<Fragment> = ArrayList()
        list.add(EmojiOneFragment())
        list.add(EmojiTwoFragment())
        //设置预加载的Fragment页面数量，可防止流式布局StaggeredGrid数组越界错误。
        //binding.emojiViewpager.setOffscreenPageLimit(list.size - 1) //设置适配器
        val adapter: FragmentStateAdapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return list[position]
            }

            override fun getItemCount(): Int {
                return list.size
            }
        }
        binding.emojiViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.emojiViewpager.setAdapter(adapter) //把适配器添加给ViewPager2
    }

    //选择某个表情的监听
    override fun onEmojiClick(text: String?) {
        Utils.utilToast(this, text.toString())
        binding.input.setText(text)
    }

    //是否弹出选择表情
    fun choseEmoji() {
        var b = true
        val el = binding.emojiLinear
        val params: ConstraintLayout.LayoutParams = el.getLayoutParams() as ConstraintLayout.LayoutParams
        params.bottomMargin = -1000
        el.layoutParams = params //使layout更新
        binding.emojiButton.setOnClickListener {
            if (b){
                Utils.utilToast(this,"调用方法")
//                el.visibility = View.GONE
                b = false

                //params.addRule(RelativeLayout.RIGHT_OF, R.id.imageView_more)
                params.bottomMargin = 0
                el.layoutParams = params //使layout更新
            }else {
//                el.visibility = View.VISIBLE
                b = true
                params.bottomMargin = -1000
                el.layoutParams = params //使layout更新
            }

        }
    }


}