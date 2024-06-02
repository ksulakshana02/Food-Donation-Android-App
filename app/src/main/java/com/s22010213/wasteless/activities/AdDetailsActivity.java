package com.s22010213.wasteless.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.Utils;
import com.s22010213.wasteless.adapters.AdapterImageSlider;
import com.s22010213.wasteless.databinding.ActivityAdDetailsBinding;
import com.s22010213.wasteless.fragment.DonationFragment;
import com.s22010213.wasteless.models.ModelAd;
import com.s22010213.wasteless.models.ModelImageSlider;

import java.util.ArrayList;
import java.util.HashMap;

public class AdDetailsActivity extends AppCompatActivity {

    private ActivityAdDetailsBinding binding;
    private static final String TAG = "AD_DETAILS_TAG";

    private FirebaseAuth firebaseAuth;
    //ad id, will get from intent
    private String adId = "";
    //latitude & longitude ot the ad to view it on map
    private double adLatitude = 0;
    private double adLongitude = 0;
    //load donor info , chat with donate, and call
    private String donorUid = null;
    private String donorPhone = "";

    //list of ad's images to show in slider
    private ArrayList<ModelImageSlider> imageSliderArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //hide some Ui Views in start
        binding.toolbarEdit.setVisibility(View.GONE);
        binding.toolbarDelete.setVisibility(View.GONE);
        binding.chatBtn.setVisibility(View.GONE);
        binding.callBtn.setVisibility(View.GONE);
        binding.mapBtn.setVisibility(View.GONE);

        //get the id of the ad
        adId = getIntent().getStringExtra("adId");
        if (adId == null){
            Log.e(TAG,"adId is null");
            finish();
            return;
        }

        Log.d(TAG,"onCreate: adId: "+ adId);

        //firebase auth for auth related tasks
        firebaseAuth = FirebaseAuth.getInstance();

        loadAdDetails();
        loadAdImages();

        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle toolbarDelete click, delete ad
        binding.toolbarDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alert dialog to confirm if the user really wan to delete the ad
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(AdDetailsActivity.this);
                materialAlertDialogBuilder.setTitle("Delete Ad")
                        .setMessage("Are you sure you want to delete this Ad?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Delete clicked, delete ad
                                deleteAd();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //cannot clicked, dismiss dialog
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        //handle toolbarEdit click, start adCreateActivity to edit this ad
        binding.toolbarEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOptions();
            }
        });

        //handle donorProfileCv click, start donorProfileActivity
        binding.donateProfileCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdDetailsActivity.this, AdDonorProfileActivity.class);
                intent.putExtra("donorUid",donorUid);
                startActivity(intent);
            }
        });

        binding.chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.callIntent(AdDetailsActivity.this, donorPhone);
            }
        });

        //handle mapBtn click, open map with as location
        binding.mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.mapIntent(AdDetailsActivity.this,adLatitude,adLongitude);
            }
        });
    }

    private void editOptions(){
        Log.d(TAG,"editOption: ");
        //init popup menu
        PopupMenu popupMenu = new PopupMenu(this,binding.toolbarEdit);
        //add menu item to popupMenu with params
        popupMenu.getMenu().add(Menu.NONE,0,0,"Edit");
        popupMenu.getMenu().add(Menu.NONE,1,1,"Mark as Completed");
        //popupMenu show
        popupMenu.show();

        //handle popup menu item click
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == 0){
                    //edit clicked
                    Intent intent = new Intent(AdDetailsActivity.this, DonationFragment.class);
                    intent.putExtra("isEditMode", true);
                    intent.putExtra("adId", adId);
                    startActivity(intent);

                }else if (itemId == 1){
                    //mark as completed
                    showMarkAsCompleteDialog();
                }
                return true;
            }
        });
    }

    private void showMarkAsCompleteDialog(){
        //material alert dialog
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Mark as Completed")
                .setMessage("Are you sure you want to mark this ad as completed?")
                .setPositiveButton("COMPLETED", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick:  completed clicked...");

                        //setup info to update in the existing ad
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", "" + Utils.AD_STATUS_COMPLETED);

                        //ad's path to update itd available/completed status
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
                        ref.child(adId)
                                .updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //success
                                        Log.d(TAG,"onSuccess: marked as completed");
                                        Utils.toast(AdDetailsActivity.this,"Marked as Completed");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failure
                                        Log.d(TAG,"onFailure: ",e);
                                        Utils.toast(AdDetailsActivity.this, "Failed to mark as completed due to "+ e.getMessage());
                                    }
                                });
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: Cancel clicked...");
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void loadAdDetails(){
        Log.d(TAG, "loadAdDetails");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            ModelAd modelAd = snapshot.getValue(ModelAd.class);

                            donorUid = modelAd.getUid();
                            String title = modelAd.getTitle();
                            String description = modelAd.getDescription();
                            String address = modelAd.getAddress();
                            String foodType = modelAd.getFood_type();
                            String quantity = modelAd.getQuantity();
                            adLatitude = modelAd.getLatitude();
                            adLongitude = modelAd.getLongitude();
                            long timestamp = modelAd.getTimestamp();

                            //format if the ad is by currently signed-in user
                            String formattedDate = Utils.formatTimestampDate(timestamp);

                            if (donorUid.equals(firebaseAuth.getUid())){
                                //ad is created by currently signed im user so
                                binding.toolbarEdit.setVisibility(View.VISIBLE);
                                binding.toolbarDelete.setVisibility(View.VISIBLE);
                                binding.chatBtn.setVisibility(View.GONE);
                                binding.callBtn.setVisibility(View.GONE);
                                binding.mapBtn.setVisibility(View.GONE);
                                binding.donateProfileCv.setVisibility(View.GONE);

                            }else {
                                //ad is not created by currently signed in user so
                                binding.toolbarEdit.setVisibility(View.GONE);
                                binding.toolbarDelete.setVisibility(View.GONE);
                                binding.chatBtn.setVisibility(View.VISIBLE);
                                binding.callBtn.setVisibility(View.VISIBLE);
                                binding.mapBtn.setVisibility(View.VISIBLE);
                                binding.donateProfileCv.setVisibility(View.VISIBLE);
                            }

                            //set data to ui
                            binding.titleTv.setText(title);
                            binding.descriptionTv.setText(description);
                            binding.addressTv.setText(address);
                            binding.foodTypeTv.setText(foodType);
                            binding.dateTv.setText(formattedDate);
                            binding.quantityTv.setText(quantity + " Available");

                            //function call, load donor info
                            loadDonorDetails();

                        } catch (Exception e){
                            Log.d(TAG, "onDataChange: ",e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadDonorDetails(){
        Log.d(TAG,"loadDonorDetails: ");
        //db path to load donor info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(donorUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String phoneNumber = ""+ snapshot.child("phoneNumber").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        String profileImageUrl = "" + snapshot.child("profileImageUrl").getValue();
                        long timestamp = (long) snapshot.child("timestamp").getValue();
                        //format date time
                        String formattedDate = Utils.formatTimestampDate(timestamp);
                        //set data to ui
                        binding.donorNameTv.setText(name);
                        binding.memberSinceTv.setText(formattedDate);
                        donorPhone = phoneNumber;

                        try {
                            Glide.with(AdDetailsActivity.this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ic_profile)
                                    .into(binding.profileIv);
                        }catch (Exception e){
                            Log.e(TAG,"onDataChange",e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAdImages(){
        //init list before starting adding data into it
        imageSliderArrayList = new ArrayList<>();

        //db path to load the ad images
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId).child("Images")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before starting adding data into it
                        imageSliderArrayList.clear();

                        //there might be multiple images, loop it to load all
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //prepare model
                            ModelImageSlider modelImageSlider = ds.getValue(ModelImageSlider.class);
                            //add the prepared model to list
                            imageSliderArrayList.add(modelImageSlider);
                        }
                        //setup adapter and set to viewpage
                        AdapterImageSlider adapterImageSlider = new AdapterImageSlider(AdDetailsActivity.this, imageSliderArrayList);
                        binding.imageSliderVp.setAdapter(adapterImageSlider);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void deleteAd(){
        Log.d(TAG, "deleted: ");

        //db path to delete the ad
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //success
                        Log.d(TAG,"onSuccess: Deleted");
                        Utils.toast(AdDetailsActivity.this, "Deleted");
                        //finish activity and go back
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failure
                        Log.e(TAG,"onFailure: ",e);
                        Utils.toast(AdDetailsActivity.this,"Failed to delete due to "+ e.getMessage());
                    }
                });
    }

}