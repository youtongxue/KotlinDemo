package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.bean.Fruit

class FruitListViewSgAdapter(val fruitList: List<Fruit>): RecyclerView.Adapter<FruitListViewSgAdapter.ViewHolder>() {

    //内部类ViewHolder，   View参数通常为RecyclerView子项的最外层布局
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val fruitImage: ImageView = view.findViewById(R.id.imageView_sg)
        val fruitName: TextView = view.findViewById(R.id.textView_sg)
        val fruitCard: CardView = view.findViewById(R.id.recycler_card)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //创建ViewHolder实例
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_sg_item, parent, false)//加载布局
        //设置 View的点击事件,这里的View为item布局文件view group
        val viewHolder = ViewHolder(view)
        viewHolder.fruitName.setOnClickListener {
            val position = viewHolder.adapterPosition
            val fruit = fruitList[position]
            Toast.makeText(parent.context, "点击了水果名 ： ${fruit.name}", Toast.LENGTH_SHORT).show()
        }

        viewHolder.fruitCard.setOnClickListener {
            val position = viewHolder.adapterPosition
            val fruit = fruitList[position]
            Toast.makeText(parent.context, "点击了Card ： ${fruit.name}", Toast.LENGTH_SHORT).show()
        }
        return viewHolder
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