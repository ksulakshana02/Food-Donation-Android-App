package com.s22010213.wasteless;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Locale;

//a class that will contain static function, constants, variables that we will be used in whole application
public class Utils {

    public static final String[] foodType = {"Veg", "Non-Veg", "Both"};
    public static final String AD_STATUS_COMPLETED = "COMPLETED";

    private static  final String CHARACTERS = "0123456789";
    private static final int OTP_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

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

    public static String generateOtp(){
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i< OTP_LENGTH; i++){
            otp.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return otp.toString();
    }

}
