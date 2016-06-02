package com.kvana.streamhub_android_sdk.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by Hari on 02/06/16.
 */
public class Util {
    public static String stringToBase64(String string) {
        byte[] data = new byte[0];
        try {
            data = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        return base64.trim();
    }

}
