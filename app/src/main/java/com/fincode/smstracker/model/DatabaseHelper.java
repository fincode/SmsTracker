package com.fincode.smstracker.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fincode.smstracker.model.entities.Message;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "db.sqlite";
    public static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<Message, Integer> messageDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        dropTables();
        createTables();
    }

    private void createTables() {
        try {
            TableUtils.createTable(connectionSource, Message.class);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    private void dropTables() {
        try {
            TableUtils.dropTable(connectionSource, Message.class, false);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Can't drop tables", e);
            throw new RuntimeException(e);
        }
    }

    public void clearTables() {
        try {
            TableUtils.clearTable(connectionSource, Message.class);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Can't drop tables", e);
            throw new RuntimeException(e);
        }
    }

    public RuntimeExceptionDao<Message, Integer> getMessageDao() {
        if (messageDao == null) {
            messageDao = getRuntimeExceptionDao(Message.class);
        }
        return messageDao;
    }

    @Override
    public void close() {
        super.close();
        messageDao = null;
    }
}
