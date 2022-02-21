package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class RecAdapter(var context: Context, private val layoutId: Int, private val data: List<Any>) : RecyclerView.Adapter<RecAdapter.MyViewHolders?>() {
    private var pos = 0


    inner class MyViewHolders(view: View): RecyclerView.ViewHolder(view){

        var text: TextView = view.findViewById(R.id.textView27)
        val linear: LinearLayout = view.findViewById(R.id.rec_item_linear)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolders {
        val v = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MyViewHolders(v)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    private var listener: OnMyItemClickListener? = null
    fun setOnMyItemClickListener(listener: OnMyItemClickListener?) {
        this.listener = listener
    }

    interface OnMyItemClickListener {
        fun myClick(pos: Int)
    }


    override fun onBindViewHolder(holder: MyViewHolders, position: Int) {
        //渲染Item数据
        holder.text.text = data[position] as CharSequence?

        //更改Item背景
        if (position == pos) {
            holder.linear.background = getDrawable(context, R.drawable.item_shape_radio_c)
        } else {
            holder.linear.background = null
        }

        //设置Item OnClick回调
        var clickPosition = holder.layoutPosition //当前 点击Item 位置
        if (listener != null) {
            holder.itemView.setOnClickListener { listener!!.myClick(clickPosition) }
        }

    }

    fun refreshBg(viewPagerPosition: Int, lastPosition: Int) {
        this.pos = viewPagerPosition
        //notifyDataSetChanged()//更新全部数据

        //更新某个Item数据，有默认动画
        notifyItemChanged(viewPagerPosition)//更新当前选中Item数据 -> 添加背景
        notifyItemChanged(lastPosition)//更新上次选中Item数据 -> 删除背景
    }




}