package com.example.myapplication.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class EmojiMiniAdapter(var context: Context, val data: List<Drawable>) : RecyclerView.Adapter<EmojiMiniAdapter.MyViewHolders?>() {
    private var pos = 0


    inner class MyViewHolders(view: View): RecyclerView.ViewHolder(view){
        var emojiImg: ImageView = view.findViewById(R.id.emojimini_img)
        val linear: LinearLayout = view.findViewById(R.id.emojimini_linear)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolders {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.emojimini_item, parent, false)
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
        holder.emojiImg.setImageDrawable(data[position])

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