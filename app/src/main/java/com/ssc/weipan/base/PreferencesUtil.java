package com.ssc.weipan.base;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yeungeek on 14-11-3.
 */
public class PreferencesUtil {
    public static String PREFERENCE_NAME = "Tulip2";

    /**
     * 
     * @param context
     * @param key
     * @param value
     * @return
     */
    public static boolean putString(final Context context, final String key,
            final String value) {
        SharedPreferences settings = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * 
     * @param context
     * @param key
     * @return
     */
    public static String getString(final Context context, final String key) {
        return getString(context, key, "");
    }

    /**
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(final Context context, final String key,
            final String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    /**
     * put boolean preferences
     * 
     * @param context
     * @param key
     * @param value
     * @return True if the new values were successfully written to persistent
     *         storage.
     */
    public static boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * get boolean value by key,default is false
     * 
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /**
     * get boolean preferences
     * 
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(Context context, String key,
            boolean defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }
}
