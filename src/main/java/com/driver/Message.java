package com.driver;

import java.sql.Timestamp;
import java.util.Date;
public class Message {
    private int id;
    private String content;
    private Date timestamp;

    public Message(String content) {
        this.content = content;
//        Timestamp ts = new Timestamp(System.currentTimeMillis());
        this.timestamp = new Date();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
