package com.s22010213.wasteless;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.*;

public class FcmNotificationSender {

    private static final String FCM_URL = "https://fcm.googleapis.com/v1/projects/wasteless-c15c5/messages:send";


    public static void sendNotificationToAllUsers(Map<String,String> userToken, String title, String body) {
        for (String token : userToken.values()) {
            sendNotification(token, title, body);
        }
    }

    private static void sendNotification(String token, String title, String body){
        OkHttpClient client = new OkHttpClient();
        String accessToken = FcmHelper.getAccessToken();
        if (accessToken == null){
            throw new IllegalArgumentException("Token cannot be null");
        }


        JSONObject message= new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject messageBody = new JSONObject();

        try {
            messageBody.put("token", token);
            notification.put("title", title);
            notification.put("body", body);
            messageBody.put("notification",notification);
            message.put("message", messageBody);
        }catch (JSONException e){
            e.printStackTrace();
            return;
        }


        RequestBody requestBody = RequestBody.create(
                message.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(FCM_URL)
                .addHeader("Authorization", "Bearer "+ accessToken)
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    System.err.println("Failed to send notification: " + response.body().string());
                    throw new IOException("Unexpected code "+ response);
                }
                System.out.println("Notification sent: "+ response.body().string());
                Log.d("fcm", response.body().string());
            }
        });

    }
}
