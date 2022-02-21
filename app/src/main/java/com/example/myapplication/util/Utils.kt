package com.example.myapplication.util

import android.content.Context
import android.os.Build
import android.widget.Toast

fun main(){
    //demo(123)
    //withFun()
    //runFun()
    applyFun()
}

/**
 * @Description kotlin 字符串的拼接
 * */
fun demoString(num: Int , string: String = "this is string ") {
    println("num is = $num    sting is = $string")
}

/**
 * @Description Kotlin with 函数  P131
 *@author OneStep
 * */
fun withFun(){
    val list = listOf("Apple", "Banana", "Orange", "Pear", "Grape")
    val result = with(StringBuffer()){
        append("Start eating fruits.\n")
        for (fruit in list){
            append(fruit).append("\n")
        }
        append("Ate all fruits.")
        toString()
    }
    println(result)
}

/**
 * @Description Kotlin run 函数  P132
 *@Date 2022-1-26
 * @author OneStep
 * */
//val result = object.run {  } //结构体
fun runFun(){
    val list = listOf("Apple", "Banana", "Orange")
    val result = StringBuffer().run{
        append("Start >>>>>>   "+"\n")
        for (fruit in list){
            append(fruit).append("\n")
        }
        append("End >>>>>> ")
        toString()
    }
    println(result)
}

/**
 * @Description Kotlin apply 函数  P133
 *@Date 2022-1-26
 * @author OneStep
 * */
//val result = object.apply { //这里是obj的上下文 } //result == obj
fun applyFun(){
    val list = listOf("Apple", "Banana", "Orange")
    val result = StringBuffer().apply{
        append("Start >>>>>>   "+"\n")
        for (fruit in list){
            append(fruit).append("\n")
        }
        append("End >>>>>> ")
    }
    println(result.toString())
}

/**
 * @Description Kotlin 类似Java中的静态方法 -> 单例类
 *@Date 2022-1-26
 * @author OneStep
 * */
class Utils {
    companion object{
        //Toast
//        @JvmStatic
        fun utilToast(context: Context, string: String) {
            Toast.makeText(context,string,Toast.LENGTH_SHORT).show()
        }

        /**
         * 获取手机品牌
         */
        fun getDeviceBrand(): String? {
            return Build.BRAND
        }

        /**
         * 获取手机型号
         */
        fun getDeviceModel(): String? {
            return Build.MODEL
        }

        //dp, px 转换工具
        fun dp2px(context: Context, dp: Double): Double =dp * context.resources.displayMetrics.density
        fun px2dp(context: Context,px:Float):Float =px / context.resources.displayMetrics.density


    }
}




