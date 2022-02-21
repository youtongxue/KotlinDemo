package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.EmojiIconItemBinding

class EmojiViewPagerAdapter(val viewList: List<String>): RecyclerView.Adapter<EmojiViewPagerAdapter.ViewHolder>() {

    //内部类ViewHolder，   View参数通常为RecyclerView子项的最外层布局
    inner class ViewHolder(val binding: EmojiIconItemBinding): RecyclerView.ViewHolder(binding.root) {
        val emojiIcon = binding.emoji

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //创建ViewHolder实例
        val binding = EmojiIconItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //对RecyclerView子项数据进行赋值
        val v = viewList[position]
        holder.emojiIcon.text = v

        holder.itemView.setOnClickListener { listener?.myClick(v) }


    }

    override fun getItemCount(): Int {
        return viewList.size
    }

    private var listener: OnMyItemClickListener? = null
    fun setOnMyItemClickListener(listener: OnMyItemClickListener?) {
        this.listener = listener
    }

    interface OnMyItemClickListener {
        fun myClick(pos: String)
    }
}