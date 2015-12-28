package com.fincode.smstracker.model;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.fincode.smstracker.app.App;
import com.fincode.smstracker.model.entities.Message;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContentManager {

    public static Message queryMessageById(int id) {
        try {
            return App.inst().getDatabase().getMessageDao().queryForId(id);
        } catch (RuntimeException e) {
            return new Message();
        }
    }

    public static void createOrUpdateMessages(final List<Message> messages, boolean fromScanningPhone) {
        try {
            App.inst().getDatabase().getMessageDao().callBatchTasks(() -> {
                RuntimeExceptionDao<Message, Integer> dao = App.inst().getDatabase().getMessageDao();
                for (Message message : messages) {
                    Message exist = queryMessage(message.getTimestamp());
                    if (exist != null) {
                        if (!fromScanningPhone) {
                            exist.setSent(message.isSent());
                            dao.update(exist);
                        }
                    } else {
                        dao.create(message);
                    }
                }
                return null;
            });
        } catch (RuntimeException e) {
        }
    }

    private static Message queryMessage(long timestamp) {
        try {
            QueryBuilder<Message, Integer> qb = App.inst().getDatabase().getMessageDao().queryBuilder();
            qb.where().eq(Message.COLUMN_TIMESTAMP, timestamp).prepare();
            List<Message> existMessages = qb.query();
            return existMessages != null && !existMessages.isEmpty() ? existMessages.get(0) : null;
        } catch (SQLException e) {
            return null;
        }
    }

    public static int updateMessage(Message Message) {
        try {
            return App.inst().getDatabase().getMessageDao().update(Message);
        } catch (RuntimeException e) {
            return 0;
        }
    }

    public static int createMessage(Message Message) {
        try {
            return App.inst().getDatabase().getMessageDao().create(Message);
        } catch (RuntimeException e) {
            return 0;
        }
    }

    public static int deleteMessageFromDb(Message message) {
        try {
            return App.inst().getDatabase().getMessageDao().delete(message);
        } catch (RuntimeException e) {
            return 0;
        }
    }

    public static List<Message> getMessages() {
        try {
            return App.inst().getDatabase().getMessageDao().queryBuilder()
                    .orderBy(Message.COLUMN_TIMESTAMP, false).query();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    public static List<Message> getMessages(boolean isSent) {
        try {
            QueryBuilder<Message, Integer> qb = App.inst().getDatabase().getMessageDao().queryBuilder().orderBy(Message.COLUMN_TIMESTAMP, false);
            qb.where().eq(Message.COLUMN_SENT, isSent).prepare();
            return qb.query();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    public static boolean clearData() {
        boolean success = true;
        try {
            App.inst().getDatabase().clearTables();
        } catch (RuntimeException e) {
            success = false;
        }
        return success;
    }

    public static List<Message> loadMessageFromPhone(Context context) {
        Uri uri = Uri.parse("content://sms/inbox");
        ContentResolver contentResolver = context.getContentResolver();
        String phoneNumber = App.inst().getPreferences().getPhoneNumber();
        boolean allMessage = phoneNumber.trim().isEmpty();
        String smsFilter = "lower(address)=lower('" + phoneNumber + "')";
        Cursor cursor = contentResolver.query(
                uri,
                new String[]{"_id", "body", "date"},
                allMessage ? null : smsFilter,
                null,
                null);
        if (cursor == null) {
            return new ArrayList<>();
        }
        cursor.moveToLast();
        List<Message> messageList = new ArrayList<Message>();
        while (cursor.moveToPrevious()) {
            String body = cursor.getString(cursor.getColumnIndex("body"));
            messageList.add(
                    new Message(
                            cursor.getString(cursor.getColumnIndex("body")),
                            cursor.getLong(cursor.getColumnIndex("date")),
                            App.inst().getPreferences().getFrom()
                    ).setSent(false)
            );
        }
        cursor.close();
        return messageList;
    }
}
