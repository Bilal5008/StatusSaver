package com.craftingapps.status.saver.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.craftingapps.status.saver.Application;


public class SharedPreferenceHelper {
    public static final String PREFERENCE_NAME = "PREFERENCE_DATA";
    public static SharedPreferenceHelper sharedPreferenceHelper;
    SharedPreferences sharedPreferences;


    public SharedPreferenceHelper() {

        sharedPreferences = Application.getContext().getSharedPreferences(AppConstants.FILE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferenceHelper getInstance() {
        if (sharedPreferenceHelper == null) {
            sharedPreferenceHelper = new SharedPreferenceHelper();
        }
        return sharedPreferenceHelper;
    }


    public void setInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public void setIntegerValue(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getIntegerValue(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void setFloatValue(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public float getFloatValue(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }


    public void setBooleanValue(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void setBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void setStringValue(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getStringValue(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void setLongValue(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public long getLongValue(String key, Long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

}
