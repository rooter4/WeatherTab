package com.example.weathertab;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class UNITS<input> {

    public static final String temp_c = "temp_c";
    public static final String temp_f = "temp_f";
    public static final String px_in = "altim_in_hg";
    public static final String px_mb = "sea_level_pressure_mb";
    public static final String NM = "NM";
    public static final String KM = "KM";
    public static final String SM = "SM";
    public static final String M = "M";
    public static String TEMP;
    public static String PX;
    public static String DIST;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor prefEditor;



    public static void init(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        prefEditor = preferences.edit();

        TEMP = preferences.getString("TEMP", temp_c);
        PX = preferences.getString("PX", px_in);
        DIST = preferences.getString("DIST", SM);
    }
    public static void change(String key, String input){
        prefEditor.putString(key, input);
        prefEditor.commit();
    }
    public static String convertToF(String c){
        try {
            if (!c.equals("n/a"))
                return String.valueOf((Double.parseDouble(c) * 1.8) + 32).substring(0, 4);
            else
                return c;
        }catch (NumberFormatException e)
        {
            return "";
        }
    }
    public static String convertToMb(String in){
        if(!in.isEmpty())
            return String.valueOf(Double.parseDouble(in) * 33.8639);
        else
            return "";
    }
    public static String convertToIn(String s){
        if(!s.isEmpty())
            return String.valueOf(Double.parseDouble(s) *0.0295301);
        else
            return "";
    }
    public static String convertToNm(String km)
    {
        if(!km.isEmpty())
            return String.valueOf(Double.parseDouble(km) / 1.852);
        return km;
    }
    public static String convertToKm(String nm)
    {
        if(!nm.isEmpty())
            return String.valueOf(Double.parseDouble(nm)* 1.852);
        return nm;
    }
}
