package in.aviaryan.cfbuddy.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.data.PrefUtils;
import in.aviaryan.cfbuddy.ui.MainActivity;

public class FCMService extends FirebaseMessagingService {
    private String TAG = "CFLOG_FCM";
    public FCMService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> map = remoteMessage.getData();
        if (map.containsKey("probId")){
            Log.d(TAG, map.get("probId"));
        }
        sendNotification(remoteMessage.getNotification().getBody());
    }

    // https://www.simplifiedcoding.net/firebase-cloud-messaging-tutorial-android/
    private void sendNotification(String messageBody) {
        PrefUtils pf = new PrefUtils(getApplicationContext());
        if (!pf.getNotificationSetting())
            return;

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Codeforces Problem of the day")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
