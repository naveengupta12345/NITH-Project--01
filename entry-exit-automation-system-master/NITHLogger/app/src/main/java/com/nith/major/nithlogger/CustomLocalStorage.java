package com.nith.major.nithlogger;

import android.app.Activity;
import android.content.SharedPreferences;
import com.nith.major.nithlogger.user.User;
import java.io.IOException;


public class CustomLocalStorage {

    public static final String PREFS_NAME = "Globals";

    public static void set(Activity activity, String key, String value){
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }
//    Shared Preferences allow you to save and retrieve data in the form of key,value pair.
//
//    In order to use shared preferences, you have to call a method getSharedPreferences()
// that returns a SharedPreference instance pointing to the file that contains the values of preferences.

//    You can save something in the sharedpreferences by using SharedPreferences.Editor class. You will call
// the edit method of SharedPreference instance and will receive it in an editor object. Its syntax is −
//
//    Editor editor = sharedpreferences.edit();
//editor.putString("key", "value");
//editor.commit();

    public static String getString(Activity activity, String key){
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFS_NAME, 0);
        return sharedPref.getString(key, null);
    }

    public static User getUser(Activity activity) throws IOException, ClassNotFoundException {
        return (User) SerializeToString.fromString(CustomLocalStorage.getString(activity, "user"));
    }

    public static void saveUser(Activity activity, User u) throws IOException {
        set(activity, "user", SerializeToString.toString(u));
    }



}
