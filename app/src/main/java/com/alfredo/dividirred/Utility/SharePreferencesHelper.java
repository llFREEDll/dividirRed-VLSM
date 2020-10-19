package com.alfredo.dividirred.Utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferencesHelper {
    private Context context;
    private SharedPreferences sharePreferences;

    public SharePreferencesHelper(Context context, String file){
        this.context = context;
        this.sharePreferences = context.getSharedPreferences(file,context.MODE_PRIVATE);

    }
    public void write(String key, String valor){
        SharedPreferences.Editor editor = sharePreferences.edit();
        editor.putString(key,valor);
        editor.apply();
    }
    public String read(String key){
        return sharePreferences.getString(key,null);

    }
    public void Clear(){
        SharedPreferences.Editor editor = sharePreferences.edit();
        editor.clear();
        editor.apply();

    }
}
