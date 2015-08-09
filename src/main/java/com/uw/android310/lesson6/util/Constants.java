package com.uw.android310.lesson6.util;


public class Constants {

    /**
     *  Client ID for Android310 app registered with Imgur, oauth2.0 without callback url
     */
    public static final String IMGUR_CLIENT_ID = "e4d167657138b91";

    /**
     *  Client Secret for Android310 app registered with Imgur
     */
    public static final String IMGUR_CLIENT_SECRET = "9b49e45ee218813465879c14c4de7283127a7787";

    public static final boolean LOGGING = false;

    /*
      Client Auth
     */
    public static String getClientAuth() {
        return "Client-ID " + IMGUR_CLIENT_ID;
    }


    //this is the name of the shared preferences file
    public static final String sharedPrefName = "hw2_prefs";

    //this is the name of the key containing the user that is logged in. used as an extra in the intent
    public static final String userName = "userName";

}
