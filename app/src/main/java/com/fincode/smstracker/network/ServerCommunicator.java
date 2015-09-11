package com.fincode.smstracker.network;


import com.fincode.smstracker.App;
import com.fincode.smstracker.BuildConfig;
import com.fincode.smstracker.Utils;
import com.fincode.smstracker.model.entities.Message;
import com.fincode.smstracker.network.exception.NoConnectivityException;

import java.io.IOException;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;

public class ServerCommunicator {

    private final IWebService mWebService;

    // Создание экземпляра подключения
    public ServerCommunicator(String endpointUrl) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endpointUrl)
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setErrorHandler(new RetrofitErrorHandler())
                .setClient(new WrappedClient(new OkClient()))
                .build();

        mWebService = restAdapter.create(IWebService.class);
    }

    public class WrappedClient implements Client {
        public WrappedClient(Client wrappedClient) {
            this.wrappedClient = wrappedClient;
        }

        Client wrappedClient;

        @Override
        public Response execute(Request request) throws IOException {
            if (!Utils.isNetworkAvailable()) {
                throw new NoConnectivityException("No connectivity");
            }
            return wrappedClient.execute(request);
        }
    }

    // Отправка сообщений
    public Boolean sendMessages(List<Message> messages) {
        String endpoint = App.inst().getPreferences().getEndpoint();
        int lastSlashIndex = endpoint.lastIndexOf('/');
        String method = lastSlashIndex == -1 ? "" : endpoint.substring(lastSlashIndex + 1);
        Message.Response res = mWebService.sendMessages(method, messages);
        return res != null && res.getResult();
        //return Utils.isNetworkAvailable();
    }

}