package org.kaldi.demo;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class PersistantStorage {
    public static final String STORAGE_NAME = "StorageName";

    private static SharedPreferences settings = null;
    private static SharedPreferences.Editor editor = null;
    private static Context context = null;

    public static PersistantStorage init(Context cntxt ){
        context = cntxt;
        return null;
    }

    private static void init(){
        settings = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    public static void addProperty( String name, String value ){
        if( settings == null ){
            init();
        }
        editor.putString( name, value );
        editor.apply();
    }

    public static void addPropertyInt( String name, int value ){
        if( settings == null ){
            init();
        }
        editor.putInt( name, value );
        editor.apply();
    }

    public static void addPropertySet(String name, Set<String> value ){
        if( settings == null ){
            init();
        }
        editor.putStringSet( name, value );
        editor.apply();
    }

    public static String getProperty( String name ){
        if( settings == null ){
            init();
        }
        return settings.getString( name, "_" );
    }

    public static int getPropertyInt(String name){
        if (settings == null){
            init();
        }
        return settings.getInt(name, 0);
    }

    public static Set<String> getPropertySet(String name){
        if (settings == null){
            init();
        }
        Set<String> namesDefault = new HashSet<String>();//дефолтный пустой сет
        //namesDefault.add("");
        return settings.getStringSet(name, namesDefault);
    }

    public static void delete(String name){
        if (settings == null){
            init();
        }
        editor.remove(name);
    }
}