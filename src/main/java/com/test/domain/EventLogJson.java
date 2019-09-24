package com.test.domain;

import org.json.simple.JSONObject;

import java.util.Set;

/**
 * Created by Jakub on 24.09.2019.
 */
public class EventLogJson extends JSONObject {

    public EventLogJson(JSONObject jsonObject){
        super();
        this.putAll(jsonObject);
    }

    public String getId(){
        return (String)this.get("id");
    }

    public String getState(){
        return (String)this.get("state");
    }

    public Long getTimestamp(){
        return (Long)this.get("timestamp");
    }

    public String getHost(){
        return (String)this.get("host");
    }

    public String getType(){
        return (String)this.get("type");
    }
}
