package com.fincode.smstracker.model.entities;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@DatabaseTable(tableName = Message.TABLE_NAME)
public class Message {

    public static final String TABLE_NAME = "messages";
    public static final String COLUMN_ID = "id";

    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String COLUMN_SENT = "isSent";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int id;
    @DatabaseField(columnName = COLUMN_TIMESTAMP, unique = true)
    private long timestamp;
    @DatabaseField(columnName = COLUMN_TEXT)
    private String text;
    @DatabaseField(columnName = COLUMN_SENT)
    private boolean isSent;

    public Message() {
    }

    public Message(String text, long timestamp) {
        this.text = text;
        this.timestamp = timestamp;
    }

    public boolean isSent() {
        return isSent;
    }

    public Message setSent(boolean isSent) {
        this.isSent = isSent;
        return this;
    }

    public int getId() {
        return id;
    }

    public Message setId(int id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public Message setText(String text) {
        this.text = text;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Message setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getFormatedDate() {
        DateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
        return sdf.format(new Date(timestamp));
    }

    public static class Request {
        private List<Message> messages;

        public Request setMessages(List<Message> messages) {
            this.messages = messages;
            return this;
        }

        public Request() {
        }
    }

    public static class Response {
        private boolean result;

        public boolean getResult() {
            return result;
        }
    }
}
