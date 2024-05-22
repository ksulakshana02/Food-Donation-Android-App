package com.s22010213.wasteless;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateFormat;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Locale;

//a class that will contain static function, constants, variables that we will be used in whole application
public class Utils {

    public static final String[] foodType = {"Veg", "Non-Veg", "Both"};
    public static final String AD_STATUS_COMPLETED = "COMPLETED";

    private static final String CHANNEL_ID = "default_channel";
    private static final int NOTIFICATION_ID = 1;

    public static void toast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static long getTimestamp(){
        return System.currentTimeMillis();
    }

    public static String formatTimestampDate(Long timestamp){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd/MM/yyyy",calendar).toString();
        return date;
    }

    //launch call intent with phone number
    public static void callIntent(Context context, String phone){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+ Uri.encode(phone)));
        context.startActivity(intent);
    }

    //launch google map with input location
    public static void mapIntent(Context context, double latitude, double longitude){
        //create a uri from an intent string, use the result to create an intent

        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" + latitude+ ","+ longitude);

        //create an intent from gmmIntentUri, set the action to action_view
        Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
        //make the intent explicit by setting the google maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null){
            context.startActivity(mapIntent);

        }else {
            Utils.toast(context,"Google MAP Not installed!");
        }
    }

    public static void sendNotification(Context context, String messageBody) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_profile)
                .setContentTitle("New Donation")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
