package csell.com.vn.csell.services;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLFriends;

/**
 * Created by cuong.nv on 4/5/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try {
            if (remoteMessage.getData() == null) {

                if (remoteMessage.getNotification() != null) {
                    String body = remoteMessage.getNotification().getBody() + "";
                    String title = remoteMessage.getNotification().getTitle() == null ? "Thông báo mới" : "Có thông báo";
                    sendNotification(title, body, null);
                }
            } else {

                Map<String, String> notification = remoteMessage.getData();
                String body = notification.get("body");
                String title = notification.get("title");
                if (title == null || body == null) {
                    sendNotification(getApplicationContext().getString(R.string.notification), getApplicationContext().getString(R.string.body_fbid_notification_new), null);
                } else {
                    sendNotification(title, body, notification);
                }

            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        }
    }

    private void sendNotification(String title, String messageBody, Map<String, String> map) {

        try {

            String type = map.get("type");

            if (type.equals("3")) {

                String[] itemsAccept = map.get("key").split(":");

                UserRetro friend1 = new UserRetro();
                friend1.setUid(itemsAccept[0]);
                friend1.setUsername(itemsAccept[1]);
                friend1.setDisplayname(itemsAccept[2]);

                SQLFriends sqlFriends = new SQLFriends(this);
                sqlFriends.insertAddFriend(friend1);
            }

//            String channelId = getString(R.string.default_notification_channel_id);
            String channelId = "csell.com.vn.csell.ONE";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Intent switchIntent = new Intent(this, switchButtonListener.class);

            switchIntent.putExtra("type_noti", map.get("type"));
            switchIntent.putExtra("key_noti", map.get("key"));

            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(getApplication(), 0,
                    switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_csell_logo_notification)
                            .setContentTitle(title)
                            .setContentText(messageBody)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(messageBody))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingSwitchIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableLights(false);
                channel.enableVibration(true);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }

            if (notificationManager != null) {
                notificationManager.notify("CSELL", 0 /* ID of notification */, notificationBuilder.build());
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
        }
    }

    public static class switchButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                BaseActivityTransition baseActivityTransition = new BaseActivityTransition(context);

                FileSave fileGet = new FileSave(context, Constants.GET);
                Intent detail;
                Bundle bundle = intent.getExtras();
                if (bundle != null) {

                    NotificationManagerCompat.from(context).cancel("CSELL", 0);

                    String type = (String) bundle.get("type_noti");
                    String key = (String) bundle.get("key_noti");

                    if (fileGet.getActiveMain()) {
                        if (fileGet.getIsCreateProduct()) {

//                            AlertDialog alertDialog = new AlertDialog.Builder(context)
//                                    .setCancelable(false)
//                                    .setTitle(R.string.you_want_exit_this_screen)
//                                    .setPositiveButton(MainActivity.mainContext.getResources().getString(R.string.agree), (dialog, which) -> {
                            Intent detail1 = new Intent(context, MainActivity.class);
                            detail1.putExtra("type_noti", type);
                            detail1.putExtra("key_noti", key);
                            detail1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(detail1);

//                                        dialog.dismiss();
//                                    })
//                                    .setNegativeButton(MainActivity.mainContext.getResources().getString(R.string.close), (dialog, which) -> {
//                                        dialog.dismiss();
//                                    })
//                                    .show();

//                            Dialog dialog = new Dialog(context);
//                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                            dialog.setContentView(R.layout.dialog_show_noti);
//                            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                            dialog.show();
//
//                            Button btnCancel = dialog.findViewById(R.id.btn_cancel);
//                            Button btnContinue = dialog.findViewById(R.id.btn_continue);
//
//                            btnContinue.setOnClickListener(v -> {
//                                dialog.dismiss();
//                            });
//
//                            btnCancel.setOnClickListener(v -> {
//                                Intent detail1 = new Intent(context, MainActivity.class);
//
//                                detail1.putExtra("type_noti", type);
//                                detail1.putExtra("key_noti", key);
//                                detail1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                context.startActivity(detail1);
//
//                                dialog.dismiss();
//                            });

//                            dialog.show();
                        } else {
                            detail = new Intent(context.getApplicationContext(), MainActivity.class);

                            detail.putExtra("type_noti", type);
                            detail.putExtra("key_noti", key);
                            detail.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.getApplicationContext().startActivity(detail);
                        }
                    } else {
                        detail = context.getPackageManager().getLaunchIntentForPackage("csell.com.vn.csell");

                        detail.putExtra("type_noti", type);
                        detail.putExtra("key_noti", key);
                        detail.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(detail);
                    }
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                Crashlytics.logException(e);
            }
        }
    }

}