package com.example.myapplication.bean

class Msg (val content: String, val type: Int, val author: String) {
    companion object{
        const val TYPE_RECEIVED = 0
        const val TYPE_SENT = 1
    }
}