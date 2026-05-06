package com.example.mescourses.network;

public class ApiConfig {

    private static final String BASE_URL = "http://192.168.1.13/user/";

    public static final String URL_LOGIN           = BASE_URL + "login.php";
    public static final String URL_REGISTER        = BASE_URL + "register.php";
    public static final String URL_CHANGE_PASSWORD = BASE_URL + "change_password.php";
    public static final String URL_CHANGE_EMAIL    = BASE_URL + "change_email.php";

    // Clés JSON réponses
    public static final String KEY_SUCCESS  = "success";
    public static final String KEY_MESSAGE  = "message";
    public static final String KEY_USER_ID  = "user_id";
    public static final String KEY_LOGIN    = "login";
    public static final String KEY_EMAIL    = "email";
}
