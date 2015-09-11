package com.fincode.smstracker.network.exception;

import java.io.IOException;

// Ошибка подключения к сети
public class NoConnectivityException extends IOException {

    private static final long serialVersionUID = 7526472295622776144L;

    public NoConnectivityException(String message) {
        super(message);
    }

    public NoConnectivityException(String message, Throwable throwable) {
        super(message, throwable);
    }

}