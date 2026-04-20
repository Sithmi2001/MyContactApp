package com.example.seu_is_20_ict_046_assignment02;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {

    private static final String PREF_NAME = "theme_pref";
    private static final String KEY_THEME = "app_theme";

    public static void applyTheme(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String theme = pref.getString(KEY_THEME, "light");

        if (theme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static void setTheme(Context context, String theme) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_THEME, theme);
        editor.apply();

        applyTheme(context);
    }
}


