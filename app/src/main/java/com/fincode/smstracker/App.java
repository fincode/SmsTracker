package com.fincode.smstracker;

import android.app.Application;

import com.fincode.smstracker.model.DatabaseHelper;
import com.fincode.smstracker.network.ServerCommunicator;
import com.fincode.smstracker.preferences.Preferences;
import com.fincode.smstracker.preferences.PreferencesHelper;

public class App extends Application {

    private static App instance;
    private DatabaseHelper database;

    private Preferences preferences;

    private ServerCommunicator serverCommunicator;

    public static App inst() {
        if (instance == null) {
            throw new IllegalStateException("You're trying to access App too early");
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = new DatabaseHelper(this);
        refreshAppSettings();

        instance = this;
    }

    public void refreshAppSettings() {
        preferences = PreferencesHelper.loadPreferences(this);
        setServerCommunicator(preferences.getEndpoint());
    }

    public void setServerCommunicator(String endpoint) {
        serverCommunicator = new ServerCommunicator(endpoint);
    }

    @Override
    public void onTerminate() {
        database.close();
        super.onTerminate();
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically
        Utils.extractLogToFile(this);
        System.exit(1);
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public ServerCommunicator getCommunicator() {
        return serverCommunicator;
    }

    public DatabaseHelper getDatabase() {
        return database;
    }
}
