package com.s22010213.wasteless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.s22010213.wasteless.models.ModelAd;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY_TAG";
    private SensorManager sensorManager;
    private Sensor proximitySensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//
//        if (proximitySensor != null) {
//            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
//        }else {
//            Log.e(TAG,"Proximity sensor not available.");
//        }
//
//        FirebaseMessaging.getInstance().subscribeToTopic("donation")
//                .addOnCompleteListener(task -> {
//                    String msg = "Subscribed to donation topic";
//                    if (!task.isSuccessful()){
//                        msg = "Subscription failed";
//                    }
//                    Log.d(TAG,msg);
//                });
    }

//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
//            float distance = event.values[1];
//            if (distance < proximitySensor.getMaximumRange()){
//                fetchAndNotifyNewDonations();
//            }
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }
//
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        if (sensorManager != null){
//            sensorManager.unregisterListener(this);
//        }
//    }
//
//    private void fetchAndNotifyNewDonations(){
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
//        ref.orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot ds: snapshot.getChildren()){
//                    ModelAd modelAd = ds.getValue(ModelAd.class);
//                    if (modelAd != null){
//                        Utils.sendNotification(MainActivity.this,"New Donation Posted: "+ modelAd.getTitle());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG,"Fetch to fetch new donation", error.toException());
//            }
//        });
//    }

}