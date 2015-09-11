package com.fincode.smstracker.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.fincode.smstracker.App;
import com.fincode.smstracker.model.entities.Message;
import com.fincode.smstracker.service.MessageSendService;

public class MessageReceivedListener extends BroadcastReceiver {

    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null &&
                ACTION.compareToIgnoreCase(intent.getAction()) == 0) {

            SmsMessage[] messages;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            } else {
                Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
                messages = new SmsMessage[pduArray.length];
                for (int i = 0; i < pduArray.length; i++) {
                    //noinspection deprecation
                    messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
                }
            }

            // Сохраняем сообщение
            String sms_from = messages[0].getDisplayOriginatingAddress();
            long timestamp = messages[0].getTimestampMillis();
            String phoneNumber = App.inst().getPreferences().getPhoneNumber();
            if (sms_from.equalsIgnoreCase(phoneNumber)) {
                StringBuilder bodyText = new StringBuilder();
                for (int i = 0; i < messages.length; i++) {
                    bodyText.append(messages[i].getMessageBody());
                }
                String body = bodyText.toString();

                Toast.makeText(context, "Sms received", Toast.LENGTH_SHORT).show();
                MessageSendService.startSendService(new Message(body, timestamp));
                // Прерывание обработки SMS другими приложениями (работает не на всех версиях)
                if (App.inst().getPreferences().isAbortSms())
                    abortBroadcast();
            }
        }
    }
}
