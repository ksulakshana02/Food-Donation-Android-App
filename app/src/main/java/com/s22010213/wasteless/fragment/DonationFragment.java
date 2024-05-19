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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.s22010213.wasteless.adapters.AdapterImagesPicked;
import com.s22010213.wasteless.activities.LocationPickerActivity;
import com.s22010213.wasteless.models.ModelImagePicked;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.Utils;
import com.s22010213.wasteless.databinding.FragmentDonationBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DonationFragment extends Fragment {

    private Context mContext;
    private FragmentDonationBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Uri imageUri = null;
    private ArrayList<ModelImagePicked> imagePickedArrayList;
    private AdapterImagesPicked adapterImagesPicked;
    private static final String TAG = "DONATION_TAG";
    private boolean isEditMode = false;
    private String adIdForEditing = "";

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

        //setup and set the condition adapter to the condition input filed
        ArrayAdapter<String> adapterFoodType = new ArrayAdapter<>(mContext,R.layout.row_foodtype_act, Utils.foodType);
        binding.foodTypeEdit.setAdapter(adapterFoodType);

        Intent intent = getActivity().getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);
        Log.d(TAG,"onCreate: isEditMode: "+ isEditMode);

        if (isEditMode){
            //Edit ad model
            adIdForEditing = intent.getStringExtra("adId");
            //function call to load ad details by using ad id
            loadAdDetails();

            //change toolbar title and submit button
            binding.toolbarTitle.setText("Update Donation");
            binding.postBtn.setText("Updated Ad");
        }else {
            binding.toolbarTitle.setText("Donation");
            binding.postBtn.setText("Post");
        }

        //init imagePickedArrayList
        imagePickedArrayList = new ArrayList<>();
        loadImages();
//        binding.toolbarBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPassed();
//            }
//        });

        //handle toolbarAddImageBtn click
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

                    //get result of location picked from lacationPickerActivity
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
        adapterImagesPicked = new AdapterImagesPicked(getContext(),imagePickedArrayList,adIdForEditing);
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
            //all data is validated
            if (isEditMode){
                updateAd();
            }else {
                postAd();
            }

        }
    }

    private void postAd(){
        Log.d(TAG, "postAd: ");
        //show progress
        progressDialog.setMessage("Publishing Ad");
        progressDialog.show();

        long timestamp = Utils.getTimestamp();
        DatabaseReference refAds = FirebaseDatabase.getInstance().getReference("Ads");
        String keyId = refAds.push().getKey();

        //setup data to add in firebase database
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

    private void updateAd(){

        progressDialog.setMessage("Updating Ad...");
        progressDialog.show();

        //setup data to add in firebase database
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("title", ""+ title);
        hashMap.put("description", ""+ description);
        hashMap.put("quantity", ""+ quantity);
        hashMap.put("cooked_time", ""+ cookedTime);
        hashMap.put("best_time", ""+ bestTime);
        hashMap.put("food_type", ""+ foodType);
        hashMap.put("address", ""+ address);
        hashMap.put("latitude", latitude);
        hashMap.put("longitude", longitude);

        //database path to update ad
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adIdForEditing)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //ad data update success
                        progressDialog.dismiss();
                        //start uploading images
                        uploadImageStorage(adIdForEditing);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // ad data update failed
                        Utils.toast(mContext,"Failed to update Ad due to "+ e.getMessage());
                    }
                });

    }

    private void uploadImageStorage(String adId){
        Log.d(TAG, "uploadImageStorage: ");
        //there are multiple images in imagePickedArrayList
        for (int i=0; i< imagePickedArrayList.size(); i++){
            //get model from the current position
            ModelImagePicked modelImagePicked = imagePickedArrayList.get(i);

            //upload image only if picked from gallery
            if (!modelImagePicked.getFromInternet()){
                //for name of the image in firebase
                String imageName = modelImagePicked.getId();
                //path and name of the image in firebase storage
                String filePathAndName = "Ads/"+ imageName;

                int imageIndexForProgress = i+1;

                //storage reference with filePathAndName
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
                                    hashMap.put("id", ""+ modelImagePicked.getId());
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

    private void loadAdDetails(){
        Log.d(TAG,"loadAdDetails: ");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adIdForEditing)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String title = ""+ snapshot.child("title").getValue();
                        String description = ""+ snapshot.child("description").getValue();
                        String quantity = ""+ snapshot.child("quantity").getValue();
                        String bestTime = ""+ snapshot.child("best_time").getValue();
                        String cookedTime = ""+ snapshot.child("best_time").getValue();
                        String foodType = ""+ snapshot.child("food_type").getValue();
                        latitude = (Double) snapshot.child("latitude").getValue();
                        longitude = (Double) snapshot.child("longitude").getValue();
                        String address = ""+ snapshot.child("address").getValue();

                        binding.titleEdit.setText(title);
                        binding.descriptionEdit.setText(description);
                        binding.quantityEdit.setText(quantity);
                        binding.cookedTimeEdit.setText(cookedTime);
                        binding.bestTimeEdit.setText(bestTime);
                        binding.foodTypeEdit.setText(foodType);
                        binding.locationEdit.setText(address);

                        DatabaseReference refImage = snapshot.child("Images").getRef();
                        refImage.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot ds: snapshot.getChildren()){
                                    String id = ""+ ds.child("id").getValue();
                                    String imageUrl = ""+ ds.child("imageUrl").getValue();

                                    ModelImagePicked modelImagePicked = new ModelImagePicked(id,null,imageUrl,true);
                                    imagePickedArrayList.add(modelImagePicked);
                                }

                                loadImages();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}