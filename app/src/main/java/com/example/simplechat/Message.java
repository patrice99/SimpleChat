package com.example.simplechat;

import com.parse.ParseObject;

public class Message extends ParseObject {
    //This model class will provide message data for the RecyclerView and will be used to retrieve and save messages to Parse
    public static final String USER_ID_KEY = "userId";
    public static final String BODY_KEY = "body";

    public String getUserIdKey() {
        return getString(USER_ID_KEY);
    }

    public String getBodyKey() {
        return getString(BODY_KEY);
    }

    public void setUserId(String userId){
        put(USER_ID_KEY, userId);
    }
    public void setBody(String body){
        put(BODY_KEY, body);
    }
}
