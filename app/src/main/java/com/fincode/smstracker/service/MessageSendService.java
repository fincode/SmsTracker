package com.fincode.smstracker.service;;

import android.app.IntentService;



import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


import com.fincode.smstracker.App;
import com.fincode.smstracker.R;
import com.fincode.smstracker.eventbus.ActivityRefreshEvent;
import com.fincode.smstracker.eventbus.BusProvider;
import com.fincode.smstracker.model.ContentManager;
import com.fincode.smstracker.model.entities.Message;
import com.fincode.smstracker.view.MainActivity;

import java.util.List;

public class MessageSendService extends IntentService {

    private static final String ARG_SMS_BODY = "sms_body";
    private static final String ARG_SMS_TIMESTAMP = "sms_timestamp";
    private static final String ARG_ACTION_SAVE_MESSAGE = "save_message";
    public static final String EVENT_REFRESH_GUI = "smstracker.intent.action.REFRESH_GUI";
    public static final String INTENT_SERVICE_DOWNLOAD = "smstracker.service.SEND";

    private static boolean isSending = false;

    public MessageSendService() {
        super("SEND_SERVICE");
    }


    public static void startSendService(Message message) {
        Intent mIntent = new Intent(MessageSendService.INTENT_SERVICE_DOWNLOAD);
        if (message != null) {
            mIntent.putExtra(ARG_ACTION_SAVE_MESSAGE, true);
            mIntent.putExtra(ARG_SMS_BODY, message.getText());
            mIntent.putExtra(ARG_SMS_TIMESTAMP, message.getTimestamp());
        } else {
            mIntent.putExtra(ARG_ACTION_SAVE_MESSAGE, false);
        }
        App.inst().startService(mIntent);
    }

    private boolean sendSms(List<Message> message) {
        try {
            return App.inst().getCommunicator().sendMessages(message);
        } catch (Exception e) {
            return false;
        }
    }

    private void showNotification(Message message) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Context context = getApplicationContext();
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message.getText())
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.getNotification();
        notificationManager.notify(R.drawable.ic_launcher, notification);
    }


    public class LocalBinder extends Binder {
        MessageSendService getService() {
            return MessageSendService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getExtras() != null
                && App.inst().getPreferences().isSendEnabled()) {


            boolean saveMessage = intent.getExtras().getBoolean(ARG_ACTION_SAVE_MESSAGE, false);
            if (saveMessage) {
                String sms_body = intent.getExtras().getString(ARG_SMS_BODY);
                long sms_timestamp = intent.getExtras().getLong(ARG_SMS_TIMESTAMP);
                ContentManager.createMessage(new Message(sms_body, sms_timestamp).setSent(false));
            }

            List<Message> messages = ContentManager.getMessages(false);
            if (messages != null && !messages.isEmpty() && !isSending) {
                isSending = true;
                boolean successSent = sendSms(messages);
                for (Message message : messages) {
                    message.setSent(successSent);
                }
                ContentManager.createOrUpdateMessages(messages, false);
                BusProvider.getInstance().post(new ActivityRefreshEvent(successSent));
                isSending = false;
            }
        }
    }

}
