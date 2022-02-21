package com.example.myapplication.util;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

public class KeyBoardListenerHelper {
    private static Boolean b = false;
    private static Boolean first = true;
    //constants
    private static final String TAG = "KeyBoardListenerHelper";
    //data
    private WeakReference<Activity> weakActivity = null;//避免内存泄漏，使用弱引用
    private OnKeyBoardChangeListener onKeyBoardChangeListener;
    private final ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!isActivityValid() || onKeyBoardChangeListener == null) {
                        return;
                    }
                    try {
                        Rect rect = new Rect();
                        weakActivity.get().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                        int screenHeight = weakActivity.get().getWindow().getDecorView().getHeight();
                        int keyBoardHeight = screenHeight - rect.bottom;
                        onKeyBoardChangeListener.OnKeyBoardChange(keyBoardHeight > 0, keyBoardHeight);
                    } catch (Exception e) {
                        Log.e(TAG, "onGlobalLayout error:" + e.getMessage());
                    }
                }
            };

    public KeyBoardListenerHelper(Activity activity) {
        if (activity == null) {
            return;
        }
        weakActivity = new WeakReference<>(activity);
        try {
            //设置后才可以监听到软键盘的弹出,此处不能设置SOFT_INPUT_ADJUST_UNSPECIFIED或者SOFT_INPUT_STATE_UNSPECIFIED，其他都可以.
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            View content = weakActivity.get().findViewById(android.R.id.content);
            content.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        } catch (Exception e) {
            Log.e(TAG, "KeyBoardListenerHelper error:" + e.getMessage());
        }
    }

    //在不使用的时候需要及时销毁，避免内存泄漏或造成额外开销
    public void destroy() {
        Log.i(TAG, "destroy");
        if (!isActivityValid()) {
            return;
        }
        try {
            View content = weakActivity.get().findViewById(android.R.id.content);
            content.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        } catch (Exception e) {
            Log.e(TAG, "destroy error:" + e.getMessage());
        }
    }

    public void setOnKeyBoardChangeListener(OnKeyBoardChangeListener listener) {
        Log.i(TAG, "setOnKeyBoardChangeListener");
        this.onKeyBoardChangeListener = listener;
    }

    public interface OnKeyBoardChangeListener {

        void OnKeyBoardChange(boolean isShow, int keyBoardHeight);
    }

    public boolean isActivityValid() {
        return weakActivity != null && weakActivity.get() != null;
    }

    public static void keyBord(Activity activity){

        KeyBoardListenerHelper keyBoardListenerHelper = new KeyBoardListenerHelper(activity);
        keyBoardListenerHelper.setOnKeyBoardChangeListener(
                new KeyBoardListenerHelper.OnKeyBoardChangeListener() {
                    @Override public void OnKeyBoardChange(boolean isShow, int keyBoardHeight) {
                        //此处可以根据是否显示软键盘，以及软键盘的高度处理逻辑
                        Log.i("testtest", "isShow: " + isShow + " keyBoardHeight:" + keyBoardHeight);
                        if (isShow & first){
                            b = isShow;
                            first = false;
                        }else {

                        }

                    }
                });

    }

}

