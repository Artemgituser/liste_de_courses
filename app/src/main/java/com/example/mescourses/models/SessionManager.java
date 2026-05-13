package com.example.mescourses.models;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME    = "MesCoursesSession";
    private static final String KEY_IS_LOGGED = "isLoggedIn";
    private static final String KEY_ID       = "userId";
    private static final String KEY_LOGIN    = "userLogin";
    private static final String KEY_EMAIL    = "userEmail";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

   //save
    public void saveSession(int userId, String login, String email) {
        editor.putBoolean(KEY_IS_LOGGED, true);
        editor.putInt(KEY_ID, userId);
        editor.putString(KEY_LOGIN, login);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    //update email
    public void updateEmail(String newEmail) {
        editor.putString(KEY_EMAIL, newEmail);
        editor.apply();
    }

   //logged
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED, true);
    }

    //id
    public int getUserId() {
        return prefs.getInt(KEY_ID, -1);
    }

    //login
    public String getLogin() {
        return prefs.getString(KEY_LOGIN, "");
    }

//email
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }


    public void logout() {
        editor.clear();
        editor.apply();
    }
}
