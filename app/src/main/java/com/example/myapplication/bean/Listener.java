package com.example.myapplication.bean;

public class Listener {
    public interface OnEmojiSelectListener {	// 创建interface类
        void onChange();    // 值改变
    }

    private static OnEmojiSelectListener onEmojiSelectListener;	// 声明interface接口
    public static void setOnEmojiSelectListener(OnEmojiSelectListener onChange){	// 创建setListener方法
        onEmojiSelectListener = onChange;
    }

    private static int posEmoji;
    public static int getPosEmoji() {
        return posEmoji;
    }
    public static void setPosEmoji(int pos) {
        Listener.posEmoji = pos;
        onEmojiSelectListener.onChange();
    }
}
