package com.uw.android310.lesson6.util;


import android.app.Activity;
import android.content.Intent;


public class IntentUtils {
    public static final int FILE_PICK = 1001;

    public static void chooseFileIntent(Activity activity){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, FILE_PICK);
    }
}
