package com.example.mobilak_p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PromoNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper.createNotificationChannel(context);
        NotificationHelper.showNotification(
                context,
                "Akciós ajánlat!",
                "20% kedvezmény minden Berlin járatra holnap estig!",
                2001
        );
    }
}
