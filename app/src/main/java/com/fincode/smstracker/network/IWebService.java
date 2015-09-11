package com.fincode.smstracker.network;


import com.fincode.smstracker.model.entities.Message;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

// Интерфейс, предоставляющий методы для работы с веб-сервисом
public interface IWebService {

    @POST("/{method}")
    public Message.Response sendMessages(@Path("method") String method, @Body List<Message> messages);
}
