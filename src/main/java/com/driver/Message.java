package com.driver;

import java.sql.Timestamp;
import java.util.Date;
public class Message {
    private int id;
    private String content;
    private Date timestamp;

    public Message(int id,String content) {
        this.id = id;
        this.content = content;
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

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
