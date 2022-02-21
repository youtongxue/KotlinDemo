package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.bean.Fruit

class FruitRecyclerViewAdapter(val fruitList: List<Fruit>): RecyclerView.Adapter<FruitRecyclerViewAdapter.ViewHolder>() {

    //内部类ViewHolder，   View参数通常为RecyclerView子项的最外层布局
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val fruitImage: ImageView = view.findViewById(R.id.imageView_list_image)
        val fruitName: TextView = view.findViewById(R.id.textView_list_item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //创建ViewHolder实例
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_demo_item, parent, false)//加载布局
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //对RecyclerView子项数据进行赋值
        val fruit = fruitList[position]
        holder.fruitImage.setImageResource(fruit.imageId)
        holder.fruitName.text = fruit.name
    }

    override fun getItemCount(): Int {
        return fruitList.size
    }
}