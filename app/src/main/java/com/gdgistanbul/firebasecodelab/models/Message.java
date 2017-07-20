package com.gdgistanbul.firebasecodelab.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by burcuturkmen on 18/07/17.
 */

@IgnoreExtraProperties
public class Message implements Serializable {
    private String userName;
    private String data;
    private Long messageTime;


    //Firebase'in objeyi parse edebilmesi için eklendi.
    public Message() {
    }

    public Message(String userName, String data, Long messageTime) {
        this.userName = userName;
        this.data = data;
        this.messageTime = messageTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Long messageTime) {
        this.messageTime = messageTime;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("userName", getUserName());
        result.put("data", getData());
        result.put("messageTime", getMessageTime());

        return result;
    }

}
