package com.example.myapplication.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;
import com.example.myapplication.ui.view.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotification(context);
    }

    private void sendNotification(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String selectedRestaurantId = task.getResult().getString("selectedRestaurantId");
                String restaurantName = task.getResult().getString("selectedRestaurantName");

                if (selectedRestaurantId != null) {

                    db.collection("users").whereEqualTo("selectedRestaurantId", selectedRestaurantId).get().addOnCompleteListener(usersTask -> {
                        if (usersTask.isSuccessful()) {
                            StringBuilder userNames = new StringBuilder();
                            int userCount = 0;
                            for (QueryDocumentSnapshot document : usersTask.getResult()) {
                                userNames.append(document.getString("userName")).append(", ");
                                userCount++;
                            }
                            if (userNames.length() > 0) {
                                userNames.setLength(userNames.length() - 2); // Remove trailing comma and space
                            }

                            String notificationText;
                            if (userCount == 1) {
                                notificationText = context.getString(R.string.notification_lonely_restaurant);
                            } else {
                                notificationText = "Participants: " + userNames + ".";
                            }

                            // Send notification
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            String channelId = "default_channel_id";
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel channel = new NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
                                notificationManager.createNotificationChannel(channel);
                            }
                            Intent notificationIntent = new Intent(context, MainActivity.class);
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                                    .setSmallIcon(R.mipmap.ic_app)
                                    .setContentTitle(context.getString(R.string.notification_meet_restaurant) + restaurantName)
                                    .setContentText(notificationText)
                                    .setAutoCancel(true)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);
                            notificationManager.notify(0, notificationBuilder.build());
                        }
                    });
                }
            }
        });
    }
}
