package com.retsi.dabijhouder;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlertReciever extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra("id", 0);
        DatabaseHelper db = new DatabaseHelper(context);
        OpdrachtItem item = db.getOpdracht(id);

        Notification notification = new NotificationCompat.Builder(context,
                context.getString(R.string.channel_huiswerk_id))
                .setContentTitle("Huiswerk!")
                .setContentText(item.getTitel()+"\n"+item.getTypeOpdracht()+"\n"+item.getVakNaam()+"\n"+item.getDatum())
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, notification);
    }
}
