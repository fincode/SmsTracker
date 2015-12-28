package com.fincode.smstracker.network;


import com.fincode.smstracker.app.App;
import com.fincode.smstracker.BuildConfig;
import com.fincode.smstracker.Utils;
import com.fincode.smstracker.app.Config;
import com.fincode.smstracker.model.entities.Message;
import com.fincode.smstracker.network.exception.NoConnectivityException;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
                .setClient(new WrappedClient(new OkClient(getOkHttpClient())))
                .build();

        mWebService = restAdapter.create(IWebService.class);
    }



    private OkHttpClient getOkHttpClient() {
        OkHttpClient client = new OkHttpClient();

        client.setConnectTimeout(Config.CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(Config.READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(Config.WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.interceptors().add(chain -> {
            com.squareup.okhttp.Request request = chain.request();
            com.squareup.okhttp.Response response = chain.proceed(request);
            int tryCount = 0;
            while (!response.isSuccessful() && tryCount < Config.NETWORK_RETRY_COUNT) {
                tryCount++;
                response = chain.proceed(request);
            }
            return response;
        });
        return client;
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
        String serverUrl = App.inst().getPreferences().getServerUrl();
        int lastSlashIndex = serverUrl.lastIndexOf('/');
        String method = lastSlashIndex == -1 ? "" : serverUrl.substring(lastSlashIndex + 1);
        Message.Response res = mWebService.sendMessages(method, messages);
        return res != null && res.getResult();
    }

}