package com.example.timur.carmapanimation.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

/**
 * Created by Timur on 05.05.2018.
 */

public final class AndroidUtils {

    public static void showShortToast (Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showLongToast (Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_LONG);
    }

    private static void showToast(Context context, String msg, int length) {
        Toast.makeText(context, msg, length).show();
    }

    public static void showShortSnachbar (Activity activity, String msg){
        showSnackbar(activity, msg, Snackbar.LENGTH_SHORT);
    }

    public static void showLongSnachbar (Activity activity, String msg){
        showSnackbar(activity, msg, Snackbar.LENGTH_LONG);
    }

    private static void showSnackbar (Activity activity, String msg, int length) {
        Snackbar.make(activity.findViewById(android.R.id.content), msg, length).show();
    }


}
