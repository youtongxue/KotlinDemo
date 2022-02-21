package com.example.myapplication.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.bean.Fruit

class FruitRecyclerViewHorizontalAdapter(val fruitList: List<Fruit>): RecyclerView.Adapter<FruitRecyclerViewHorizontalAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val fruitImage: ImageView = view.findViewById(R.id.imageView_recyclerview_horiztonal)
        val fruitName: TextView = view.findViewById(R.id.textView_recyclerview_horiztonal)
        val fruitLinear: LinearLayout = view.findViewById(R.id.rec_linear)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_horizontal_item, parent, false)

        return MyViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val fruit = fruitList[position]
        holder.fruitImage.setImageResource(fruit.imageId)
        holder.fruitName.text = fruit.name

    }

    override fun getItemCount() = fruitList.size


}