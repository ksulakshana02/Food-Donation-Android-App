package com.s22010213.wasteless.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.s22010213.wasteless.AdapterImagesPicked;
import com.s22010213.wasteless.LocationPickerActivity;
import com.s22010213.wasteless.ModelImagePicked;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.Utils;
import com.s22010213.wasteless.databinding.FragmentDonationBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DonationFragment extends Fragment {

    private Context mContext;
    private FragmentDonationBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Uri imageUri = null;
    private ArrayList<ModelImagePicked> imagePickedArrayList;
    private AdapterImagesPicked adapterImagesPicked;
    private static final String TAG = "DONATION_TAG";

    @Override
    public void onAttach(@NonNull Context context){
        mContext = context;
        super.onAttach(context);
    }

    public DonationFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDonationBinding.inflate(LayoutInflater.from(mContext),container,false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        ArrayAdapter<String> adapterFoodType = new ArrayAdapter<>(mContext,R.layout.row_foodtype_act, Utils.foodType);
        binding.foodTypeEdit.setAdapter(adapterFoodType);

        imagePickedArrayList = new ArrayList<>();
        loadImages();
//        binding.toolbarBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPassed();
//            }
//        });

        binding.toolbarCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickOption();
            }
        });

        binding.locationEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocationPickerActivity.class);
                locationPickerActivityResultLauncher.launch(intent);
            }
        });

        binding.postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }

    private ActivityResultLauncher<Intent> locationPickerActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG,"onActivityResult: ");

                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();

                        if (data != null){
                            latitude = data.getDoubleExtra("latitude", 0.0);
                            longitude = data.getDoubleExtra("longitude", 0.0);
                            address = data.getStringExtra("address");

                            Log.d(TAG, "onActivityResult: latitude: "+ latitude);
                            Log.d(TAG, "onActivityResult: longitude: "+ longitude);
                            Log.d(TAG, "onActivityResult: address: "+ address);

                            binding.locationEdit.setText(address);
                        }
                    }else {
                        Utils.toast(getActivity(),"Cancelled");
                    }
                }
            }
    );

    private void loadImages(){
        Log.d(TAG, "LoadImages");
        adapterImagesPicked = new AdapterImagesPicked(getContext(),imagePickedArrayList);
        binding.images.setAdapter(adapterImagesPicked);
    }

    private void showImagePickOption(){
        Log.d(TAG,"showImagePickOption: ");

        PopupMenu popupMenu = new PopupMenu(mContext,binding.toolbarCamera);
        
        popupMenu.getMenu().add(Menu.NONE,1,1,"Camera");
        popupMenu.getMenu().add(Menu.NONE,2,2,"Gallery");
        
        popupMenu.show();
        
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == 1){

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        String[] cameraPermission = new String[]{Manifest.permission.CAMERA};
                        requestCameraPermission.launch(cameraPermission);

                    }else {
                        String[] cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestCameraPermission.launch(cameraPermission);
                    }
                } else if (itemId == 2) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        pickImageGallery();

                    }else {
                        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                        requestStoragePermission.launch(storagePermission);
                    }
                }
                return true;
            }
        });
    }

    private ActivityResultLauncher<String> requestStoragePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    Log.d(TAG,"onActivityResult: isGranted: "+ isGranted);

                    if (isGranted){
                        pickImageGallery();
                    }else {
                        Utils.toast(getActivity(), "Storage Permission denied...");
                    }
                }
            }
    );

    private ActivityResultLauncher<String[]> requestCameraPermission = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    Log.d(TAG,"onActivityResult: ");
                    Log.d(TAG,"onActivityResult: "+ result.toString());

                    boolean areAllGranted = true;
                    for (Boolean isGranted : result.values()){
                        areAllGranted = areAllGranted && isGranted;
                    }
                    if (areAllGranted){
                        pickImageCamera();
                    }else {
                        Utils.toast(getActivity(),"Camera or Storage or both permission denied...");
                    }
                }
            }
    );

    private void pickImageGallery(){
        Log.d(TAG,"pickImageGallery: ");

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private void pickImageCamera(){
        Log.d(TAG,"pickImageCamera: ");

        ContentValues contentValues= new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"TEMPORARY_IMAGE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"TEMPORARY_IMAGE_DESCRIPTION");

        imageUri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG,"onActivityResult: ");
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG,"onActivityResult: imageUri: "+ imageUri);
                        String timeStamp = "" + Utils.getTimestamp();

                        ModelImagePicked modelImagePicked = new ModelImagePicked(timeStamp,imageUri,null,false);
                        imagePickedArrayList.add(modelImagePicked);
                        loadImages();

                    }else {
                        Utils.toast(getActivity(),"Cancelled");
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG,"onActivityResult: ");
                    if (result.getResultCode() == Activity.RESULT_OK){

                        Log.d(TAG,"onActivityResult: imageUri: "+ imageUri);
                        String timeStamp = "" + Utils.getTimestamp();

                        ModelImagePicked modelImagePicked = new ModelImagePicked(timeStamp,imageUri,null,false);
                        imagePickedArrayList.add(modelImagePicked);
                        loadImages();

                    }else {
                        Utils.toast(getActivity(),"Cancelled");
                    }
                }
            }
    );

    private String title = "";
    private  String description = "";
    private String quantity = "";
    private String cookedTime = "";
    private String bestTime = "";
    private String foodType = "";
    private String address = "";
    private double latitude = 0;
    private double longitude = 0;

    private void validateData(){
        Log.d(TAG, "validateData: ");

        title = binding.titleEdit.getText().toString().trim();
        description = binding.descriptionEdit.getText().toString().trim();
        quantity = binding.quantityEdit.getText().toString().trim();
        cookedTime = binding.cookedTimeEdit.getText().toString().trim();
        bestTime = binding.bestTimeEdit.getText().toString().trim();
        foodType = binding.foodTypeEdit.getText().toString().trim();
        address = binding.locationEdit.getText().toString().trim();

        if (title.isEmpty()){
            binding.titleEdit.setError("Enter Title");
            binding.titleEdit.requestFocus();

        } else if (description.isEmpty()) {
            binding.descriptionEdit.setError("Enter Description");
            binding.descriptionEdit.requestFocus();

        }else if (quantity.isEmpty()) {
            binding.quantityEdit.setError("Enter Quantity");
            binding.quantityEdit.requestFocus();

        }else if (cookedTime.isEmpty()) {
            binding.cookedTimeEdit.setError("Enter Cooked Time");
            binding.cookedTimeEdit.requestFocus();

        }else if (bestTime.isEmpty()) {
            binding.bestTimeEdit.setError("Enter Best Time");
            binding.bestTimeEdit.requestFocus();

        }else if (foodType.isEmpty()) {
            binding.foodTypeEdit.setError("Choose Food Type");
            binding.foodTypeEdit.requestFocus();

//        }else if (address.isEmpty()) {
//            binding.locationEdit.setText("Choose Location");
//            binding.locationEdit.requestFocus();


        } else if (imagePickedArrayList.isEmpty()) {
            Utils.toast(getActivity(),"Pick at least one image");
        } else {
            postAd();
        }
    }

    private void postAd(){
        Log.d(TAG, "postAd: ");

        progressDialog.setMessage("Publishing Ad");
        progressDialog.show();

        long timestamp = Utils.getTimestamp();
        DatabaseReference refAds = FirebaseDatabase.getInstance().getReference("Ads");
        String keyId = refAds.push().getKey();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+ keyId);
        hashMap.put("uid", ""+ firebaseAuth.getUid());
        hashMap.put("title", ""+ title);
        hashMap.put("description", ""+ description);
        hashMap.put("quantity", ""+ quantity);
        hashMap.put("cooked_time", ""+ cookedTime);
        hashMap.put("best_time", ""+ bestTime);
        hashMap.put("food_type", ""+ foodType);
        hashMap.put("timestamp", timestamp);
        hashMap.put("address", ""+ address);
        hashMap.put("latitude", latitude);
        hashMap.put("longitude", longitude);

        refAds.child(keyId)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSuccess: Ad Published");
                        uploadImageStorage(keyId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure: "+ e);
                        progressDialog.dismiss();
                        Utils.toast(getActivity(),"Failed to publish Ad due to "+ e.getMessage());
                    }
                });

    }

    private void uploadImageStorage(String adId){
        Log.d(TAG, "uploadImageStorage: ");

        for (int i=0; i< imagePickedArrayList.size(); i++){
            ModelImagePicked modelImagePicked = imagePickedArrayList.get(i);

            String imageName = modelImagePicked.getId();
            String filePathAndName = "Ads/"+ imageName;

            int imageIndexForProgress = i+1;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);


            storageReference.putFile(modelImagePicked.getImageUri())
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            String message = "Uploading " + imageIndexForProgress+ " of "+ imagePickedArrayList.size() + " images...\nProgress " + (int) progress + "%";
                            Log.d(TAG,"onProgress: message: "+ message);
                            progressDialog.setMessage(message);
                            progressDialog.show();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG,"onSuccess: ");
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri uploadedImageUrl = uriTask.getResult();

                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                //methana poddak aul modelImagePicked.imageUri()
                                hashMap.put("id", ""+ modelImagePicked.getImageUri());
                                hashMap.put("imageUrl", ""+ uploadedImageUrl);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
                                ref.child(adId).child("Images")
                                        .child(imageName)
                                        .updateChildren(hashMap);
                            }

                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: ",e);
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}