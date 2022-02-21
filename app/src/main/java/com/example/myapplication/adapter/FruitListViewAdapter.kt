package com.example.myapplication.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.bean.Fruit

class FruitListViewAdapter(activity: Activity, val resourceId: Int, data: List<Fruit>): ArrayAdapter<Fruit>(activity, resourceId, data) {

    inner class ViewHolder(val fruitImageView: ImageView, val fruitName: TextView)//内部类

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //convertView 为已经加载过的视图缓存， getView方法 会在每个item滑入屏幕时，每次调用，造成性能损耗
        val view: View
        //优化每次滑动获取实例，调用findViewById()
        val viewHolder: ViewHolder

        if (convertView == null) {
            Log.e("TAG", "getView: 已经有视图缓存")
            view = LayoutInflater.from(context).inflate(resourceId, parent, false)

            //
            val fruitImage: ImageView = view.findViewById(R.id.imageView_list_image)//绑定item图片
            val fruitName: TextView = view.findViewById(R.id.textView_list_item)//绑定item水果名字
            viewHolder = ViewHolder(fruitImage, fruitName)//将控件实例放在ViewHolder里
            view.tag  = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val fruit = getItem(position)//获取当前水果实例

        if (fruit != null){
            viewHolder.fruitImageView.setImageResource(fruit.imageId)
            viewHolder.fruitName.text = fruit.name
        }
        return view
    }
}