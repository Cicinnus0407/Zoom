package com.cicinnus.zoom;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * author cicinnus
 * date 2018/6/1
 */
public class ToastUtil {

    private static Handler handler = new Handler(Looper.getMainLooper());


    private static Toast toast;


    private static void show(CharSequence msg, int length) {
        if (msg == null) {
            return;
        }
        if (toast != null) {
            toast.setText(msg);
        } else {
            toast = Toast.makeText(App.getInstance(), msg, length);
        }
        toast.show();
    }

    public static void showShort(final CharSequence msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                show(msg, Toast.LENGTH_SHORT);
            }
        });
    }

    public static void showLong(final CharSequence msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                show(msg, Toast.LENGTH_LONG);
            }
        });
    }
}
