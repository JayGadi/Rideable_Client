package com.rideable.resources;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rideable.R;
import com.rideable.control.ChatActivity;

/**
 * Created by Jay on 12/5/2015.
 */
public class GCMMessageHandler extends IntentService {


    String message;
    String name;
    String adId;
    private Handler handler;
    public static final int notifyID = 9001;
    NotificationCompat.Builder builder;
    NotificationManager mNotificationManager;

    public GCMMessageHandler(){
        super("GcmIntentService");
    }

    @Override
    public void onCreate() {

        super.onCreate();
        handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        message = extras.getString("message");
        name = extras.getString("name");
        adId = extras.getString("adId");

        sendNotification(name, message, adId);

        //showToast();
        Log.i("GCM", "Received : (" + messageType + ")  " + extras.getString("message"));

        GCMBroadcastReceiver.completeWakefulIntent(intent);

    }

    private void sendNotification(String name, String message, String adId) {

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(this, ChatActivity.class);
        resultIntent.putExtra("message", message);
        resultIntent.putExtra("name", name);
        resultIntent.putExtra("adId", adId );
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Rideable")
                .setContentText("You've received new message.")
                .setSmallIcon(R.drawable.marker);

        // Set pending intent
        mNotifyBuilder.setContentIntent(resultPendingIntent);

        // Set the content for Notification
        mNotifyBuilder.setContentText("New message from " + name + ": " + message);
        // Set autocancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
        mNotificationManager.notify(notifyID, mNotifyBuilder.build());
    }


}
