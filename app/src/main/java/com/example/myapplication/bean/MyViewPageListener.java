package com.example.myapplication.bean;

public class MyViewPageListener {
    public interface OnEmojiSelectListener {	// 创建interface类
        void onChanges();    // 值改变
    }

    private static OnEmojiSelectListener onEmojiSelectListener = null;	// 声明interface接口
    public static void setOnEmojiSelectListener(OnEmojiSelectListener onChange){	// 创建setListener方法
        onEmojiSelectListener = onChange;
    }

    private static int posEmoji;
    public static int getPosEmoji() {
        return posEmoji;
    }
    public static void setPosEmoji(int pos) {
        MyViewPageListener.posEmoji = pos;
        onEmojiSelectListener.onChanges();
    }
}
