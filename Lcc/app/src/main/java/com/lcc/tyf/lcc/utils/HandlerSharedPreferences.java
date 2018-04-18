package com.lcc.tyf.lcc.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by max on 6/11/17.
 */

public class HandlerSharedPreferences {
    private SharedPreferences pref;
    private static String prefName = "MyPref";
    SharedPreferences.Editor editor;

    public HandlerSharedPreferences(Context context){
        pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = pref.edit();

    }
    public void putDriverId(int id){
        editor.putInt("driver_id",id);
        editor.commit();
    }
    public int getDriverId(){
        return pref.getInt("driver_id",0);
    }

    public void putGtdId(int id){
        editor.putInt("gtd",id);
        editor.commit();
    }
    public int getGtdId(){
        return pref.getInt("gtd",0);
    }

    public void putSellerId(int id){
        editor.putInt("seller_id",id);
        editor.commit();
    }
    public int getSellerId(){
        return pref.getInt("seller_id",0);
    }

}
