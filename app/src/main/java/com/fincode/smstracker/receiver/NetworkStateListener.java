package com.fincode.smstracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fincode.smstracker.Utils;
import com.fincode.smstracker.service.MessageSendService;

public class NetworkStateListener extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent != null && Utils.isNetworkAvailable())  {
            MessageSendService.startSendService(null);
        }
    }
}