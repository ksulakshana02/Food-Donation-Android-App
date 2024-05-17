package com.s22010213.wasteless.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.s22010213.wasteless.databinding.ActivityProfileEditBinding;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
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
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.Utils;
import com.s22010213.wasteless.fragment.ProfileFragment;

import java.util.HashMap;
import java.util.Map;

public class ProfileEditActivity extends AppCompatActivity {

    private ActivityProfileEditBinding binding;
    private static final String TAG = "PROFILE_EDIT_TAG";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference reference;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadMyInfo();

        binding.imagePickerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickDialog();
            }
        });

        binding.saveAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDeleteActivity();
            }
        });
    }

    private String name = "";
    private String email = "";
    private String phone = "";
    private String password = "";
    private void validateData(){
        name = binding.editUserName.getText().toString().trim();
        email = binding.editEmail.getText().toString().trim();
        phone = binding.editPhoneNumber.getText().toString().trim();
        password = binding.editPassword.getText().toString().trim();

        if (imageUri == null){
            updateProfileDb(null);
        }else {
            uploadProfileImageStorage();
        }
    }

    private void uploadProfileImageStorage(){
        Log.d(TAG,"uploadProfileImageStorage: ");

        progressDialog.setMessage("Uploading user profile image...");
        progressDialog.show();

        String filePathAndName = "UserImages/"+"profile_"+ firebaseAuth.getUid();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(imageUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        Log.d(TAG, "onProgress: progress: "+ progress);
                        progressDialog.setMessage("Uploading profile image. Progress: "+ (int)progress+"%");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG,"onSuccess: Uploaded");

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        while (!uriTask.isSuccessful());
                        String uploadedImageUrl = uriTask.getResult().toString();

                        if (uriTask.isSuccessful()){
                            updateProfileDb(uploadedImageUrl);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"onFailure: "+ e);
                        progressDialog.dismiss();
                        Utils.toast(ProfileEditActivity.this,"Failed to upload profile image due to "+ e.getMessage());
                    }
                });
    }

    private void updateProfileDb(String imageUrl){

        progressDialog.setMessage("Updating user info...");
        progressDialog.show();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name", ""+ name);
        hashMap.put("email", ""+ email);
        hashMap.put("phoneNumber", ""+ phone);
        hashMap.put("password", ""+ password);

        if (imageUrl != null){
            hashMap.put("profileImageUrl",""+ imageUrl);
        }

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSuccess: Info updated");
                        progressDialog.dismiss();
                        Utils.toast(ProfileEditActivity.this, "profile updated...");
                        moveHomeActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure: ",e);
                        progressDialog.dismiss();
                        Utils.toast(ProfileEditActivity.this, "Failed to update info due to "+ e.getMessage());
                    }
                });

    }

    private void moveHomeActivity(){
        startActivity(new Intent(this, ProfileFragment.class));
    }

    private void moveDeleteActivity(){
        startActivity(new Intent(this, DeleteAccountActivity.class));
    }

    private void loadMyInfo(){
        Log.d(TAG,"LoadMyInfo");

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String name = ""+ snapshot.child("name").getValue();
                        String email = ""+ snapshot.child("email").getValue();
                        String phone = ""+ snapshot.child("phoneNumber").getValue();
                        String password = ""+ snapshot.child("password").getValue();
                        String imageUrl = ""+ snapshot.child("profileImageUrl");

                        binding.editUserName.setText(name);
                        binding.editEmail.setText(email);
                        binding.editPhoneNumber.setText(phone);
                        binding.editPassword.setText(password);


                        try {
                            Glide.with(ProfileEditActivity.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_profile)
                                    .into(binding.editProfilePhoto);

                        }catch (Exception e){
                            Log.e(TAG,"onDataChange: ",e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void imagePickDialog(){

        PopupMenu popupMenu = new PopupMenu(this,binding.imagePickerFab);

        popupMenu.getMenu().add(Menu.NONE,1,1,"Camera");
        popupMenu.getMenu().add(Menu.NONE,2,2,"Gallery");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == 1){
                    Log.d(TAG,"onMenuItemClick: Camera Clicked, check if camera permission(s) granted or not");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        requestCameraPermission.launch(new String[]{Manifest.permission.CAMERA});
                    }else {
                        requestCameraPermission.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
                    }
                } else if (itemId == 2) {
                    Log.d(TAG,"onMenuItemClick: Check is storage permission is granted or not");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        pickImageGallery();
                    }else {
                        requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
                return false;
            }
        });
    }

    private ActivityResultLauncher<String[]> requestCameraPermission = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    Log.d(TAG,"onActivityResult: "+ result.toString());

                    boolean areAllGranted = true;
                    for (Boolean isGranted: result.values()){
                        areAllGranted = areAllGranted && isGranted;
                    }
                    if(areAllGranted){
                        Log.d(TAG,"onActivityResult: All Granted e.g. Camera, Storage");
                        pickImageCamera();
                    }
                    else {
                        Log.d(TAG,"onActivityResult: All or either one is denied");
                        Utils.toast(ProfileEditActivity.this, "Camera or Storage or both permission denied...");
                    }
                }
            }
    );

    private ActivityResultLauncher<String> requestStoragePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    Log.d(TAG,"onActivityResult: isGranted: "+ isGranted);

                    if (isGranted){
                        pickImageGallery();
                    }else {
                        Utils.toast(ProfileEditActivity.this, "Storage permission denied...");
                    }
                }
            }
    );

    private void pickImageCamera(){
        Log.d(TAG,"pickImageCamera: ");
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMP_TITLE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMP_DESCRIPTION");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG,"OnActivityResult: Image Captured: "+ imageUri);

                        try {
                            Glide.with(ProfileEditActivity.this)
                                    .load(imageUri)
                                    .placeholder(R.drawable.man)
                                    .into(binding.editProfilePhoto);
                        }catch (Exception e){
                            Log.d(TAG,"OnActivityResult: ",e);
                        }
                    }else {
                        Utils.toast(ProfileEditActivity.this,"Cancelled...");
                    }
                }
            }
    );

    private void pickImageGallery(){
        Log.d(TAG,"pickImageGallery: ");

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG,"OnActivityResult: Image Picked From Gallery: "+ imageUri);
                            try {
                                Glide.with(ProfileEditActivity.this)
                                        .load(imageUri)
                                        .placeholder(R.drawable.man)
                                        .into(binding.editProfilePhoto);
                            }catch (Exception e){
                                Log.e(TAG,"onActivityResult: ",e);
                            }
                    }else {
                        Utils.toast(ProfileEditActivity.this,"Cancelled...");
                    }
                }
            }
    );

}