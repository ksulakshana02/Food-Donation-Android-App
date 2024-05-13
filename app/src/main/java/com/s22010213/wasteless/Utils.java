package com.s22010213.wasteless;


import android.content.Context;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class Utils {

    public static void toast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static long getTimestamp(){
        return System.currentTimeMillis();
    }

    public static String formatTimestampDate(Long timestamp){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);

        String date = DateFormat.format("dd/MM/YYYY",calendar).toString();
        return date;
    }
}
