package com.example.myapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class MyViewPagerAdapter(val viewList: List<View>): RecyclerView.Adapter<MyViewPagerAdapter.ViewHolder>() {

    //内部类ViewHolder，   View参数通常为RecyclerView子项的最外层布局
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val vC: ConstraintLayout = view.findViewById(R.id.emoji_con)
        val img: ImageView = view.findViewById(R.id.imageView_viewpage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //创建ViewHolder实例
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewpage2_item, parent, false)//加载布局
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //对RecyclerView子项数据进行赋值
        val v = viewList[position]
        v.setLayoutParams(LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        holder.vC.addView(v)

        Log.e(" >>> |", "滑动到： $position")



    }

    override fun getItemCount(): Int {
        return viewList.size
    }
}