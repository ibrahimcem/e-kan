package com.icu.kandeneme;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title=remoteMessage.getNotification().getTitle();
        String body=remoteMessage.getNotification().getBody();
        System.out.println(title+body);
        notification(title,body);

    }
    @SuppressLint("ResourceAsColor")
    private void notification(String title, String body){
        NotificationManager notificationManager= (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        Intent intent=new Intent(getApplicationContext(),BloodSeekers.newInstance().getClass());
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String id = "id";
            String name = "name";
            String tanim = "tanim";
            int priority = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(id);
            if (notificationChannel==null){
                notificationChannel = new NotificationChannel(id,name,priority);
                notificationChannel.setDescription(tanim);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            builder = new NotificationCompat.Builder(getApplicationContext(),id);
            builder.setContentTitle(title);
            builder.setContentText(body);
            builder.setColor(getResources().getColor(R.color.tamam));
            builder.setSmallIcon(R.drawable.mobile_icon);
            builder.setAutoCancel(true);
            builder.setContentIntent(pendingIntent);
        }else {
            builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setContentTitle(title);
            builder.setColor(getResources().getColor(R.color.tamam));

            builder.setContentText(body);
            builder.setSmallIcon(R.drawable.mobile_icon);
            builder.setAutoCancel(true);
            builder.setContentIntent(pendingIntent);
            builder.setPriority(Notification.PRIORITY_HIGH);

        }
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(1,builder.build());
    }


}