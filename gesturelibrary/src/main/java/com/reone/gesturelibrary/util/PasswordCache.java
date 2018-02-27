package com.reone.gesturelibrary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PasswordCache {
    private String PASSWORD_KEY = "password";
    private SharedPreferences sharedPreferences;

    public PasswordCache(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public void save(String password){
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PASSWORD_KEY, password);
        editor.apply();
    }

    public void remove(){
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        if(sharedPref.contains(PASSWORD_KEY)){
            editor.remove(PASSWORD_KEY);
        }
        editor.apply();
    }

    public String password(){
        return getSharedPreferences().getString(PASSWORD_KEY, null);
    }

    private SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
