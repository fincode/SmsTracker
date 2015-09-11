package com.fincode.smstracker.preferences;


import android.content.Context;
import android.content.SharedPreferences;

import static com.fincode.smstracker.Constants.*;

public class PreferencesHelper {

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_NUMBER = "phone_number";
    public static final String APP_PREFERENCES_SEND_ENABLED = "send_enabled";
    public static final String APP_PREFERENCES_ABORT_SMS = "abort_sms_processing";
    public static final String APP_PREFERENCES_ENDPOINT = "endpoint";
    public static final String APP_PREFERENCES_FIRST_LAUNCH = "first_launch";

    public static void savePreferences(Context context, Preferences preferences) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putString(APP_PREFERENCES_NUMBER, preferences.getPhoneNumber());
        String endpoint = preferences.getEndpoint();
        if (endpoint.isEmpty())
            endpoint = DEFAULT_ENDPOINT;
        editor.putString(APP_PREFERENCES_ENDPOINT, endpoint);
        editor.putBoolean(APP_PREFERENCES_SEND_ENABLED, preferences.isSendEnabled());
        editor.putBoolean(APP_PREFERENCES_ABORT_SMS, preferences.isAbortSms());
        editor.apply();
    }

    public static Preferences loadPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE);
        String endpoint = preferences.getString(APP_PREFERENCES_ENDPOINT, DEFAULT_ENDPOINT);
        if (endpoint.trim().isEmpty()) {
            preferences.edit().putString(APP_PREFERENCES_ENDPOINT, DEFAULT_ENDPOINT).commit();
            endpoint = DEFAULT_ENDPOINT;
        }
        return new Preferences()
                .setEndpoint(endpoint)
                .setPhoneNumber(preferences.getString(APP_PREFERENCES_NUMBER, ""))
                .setSendEnabled(preferences.getBoolean(APP_PREFERENCES_SEND_ENABLED, true))
                .setAbortSms(preferences.getBoolean(APP_PREFERENCES_ABORT_SMS, false));
    }

    public static boolean isFirstLaunch(Context context) {
        return context.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE).getBoolean(APP_PREFERENCES_FIRST_LAUNCH, true);
    }

    public static void setFirstLaunch(Context context, boolean isFirstLaunch) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putBoolean(APP_PREFERENCES_FIRST_LAUNCH, isFirstLaunch);
        editor.apply();
    }

    public static void clear(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.clear().apply();
    }
}