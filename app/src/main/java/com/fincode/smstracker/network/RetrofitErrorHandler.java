package com.fincode.smstracker.network;


import com.fincode.smstracker.network.exception.NoConnectivityException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

// Класс-обработчик ошибок запросов
public class RetrofitErrorHandler implements ErrorHandler {

    @Override
    public Throwable handleError(RetrofitError cause) {
        Throwable c = cause.getCause();
        if (c != null && c instanceof NoConnectivityException)
            return new NoConnectivityException(c.getMessage());
        Response r = cause.getResponse();
        if (r != null) {
            try {
                // Чтение полученного ответа
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(r.getBody().in()));

                StringBuilder out = new StringBuilder();
                String newLine = System.getProperty("line.separator");
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                    out.append(newLine);
                }
                String message = out.toString();

            } catch (IOException e) {
                return e;
            }
        }
        return cause;
    }
}