package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.myapplication.R
import com.example.myapplication.bean.Msg
import com.example.myapplication.databinding.MsgLeftItemBinding
import com.example.myapplication.databinding.MsgRightItemBinding

class MsgAdapterBinding(val msgList: List<Msg>) :  RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    inner class LeftViewHolder(val binding: MsgLeftItemBinding) : RecyclerView.ViewHolder(binding.root){
        val leftMsg = binding.textViewLeftMsg
        val leftImg = binding.imageMsgLeft

    }

    inner class RightViewHolder(val binding: MsgRightItemBinding): RecyclerView.ViewHolder(binding.root){
        val rightMsg = binding.textViewRightMsg
        val rightImg = binding.imageMsgRight
    }

    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position]
        return msg.type
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = if (viewType == Msg.TYPE_RECEIVED) {
        val binding = MsgLeftItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        LeftViewHolder(binding)
    } else {
        val binding = MsgRightItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        RightViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = msgList[position]
        when (holder) {
            is LeftViewHolder -> {
                holder.leftMsg.text = msg.content
                holder.leftImg.load(R.drawable.left) {transformations(CircleCropTransformation())}
            }
            is RightViewHolder -> {
                holder.rightMsg.text = msg.content
                holder.rightImg.load(R.drawable.right) {transformations(CircleCropTransformation())}
            }
            else -> throw IllegalAccessException()
        }
    }

    override fun getItemCount() = msgList.size

}