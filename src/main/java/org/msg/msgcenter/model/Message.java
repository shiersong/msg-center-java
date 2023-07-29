package org.msg.msgcenter.model;

import java.util.UUID;

public class Message {

    private String id;

    private Object data;

    public Message(Object data) {
        this.id = UUID.randomUUID().toString();
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
