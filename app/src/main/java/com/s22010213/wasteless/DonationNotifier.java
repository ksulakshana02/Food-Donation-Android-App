package com.s22010213.wasteless;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DonationNotifier {


    public static void notifyAllUsers(String title, String body){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String,String> userTokens = new HashMap<>();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String token = ds.child("fcmToken").getValue(String.class);
                    if (token != null) {
                        Log.d("fcmToken", token);
                        userTokens.put(ds.getKey(), token);
                    }
                }

                FcmNotificationSender.sendNotificationToAllUsers(userTokens,title,body);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("loadTokens:onCancelled: "+ error.toException());
            }
        });
    }

}
