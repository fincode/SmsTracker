package com.fincode.smstracker.network;


import com.fincode.smstracker.model.entities.Message;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.POST;

// Интерфейс, предоставляющий методы для работы с веб-сервисом
public interface IWebService {

    @POST("/sms.ashx")
    public Message.Response sendMessages(@Body List<Message> messages);
}
