package com.s22010213.wasteless;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AuthManager {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private Context context;
    private ProgressDialog progressDialog;

    public AuthManager(Context context){
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.reference = FirebaseDatabase.getInstance().getReference("Users");
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setTitle("Please wait...");
        this.progressDialog.setCanceledOnTouchOutside(false);
    }

    public void registerUserWithEmail(String name, String email, String mobile, String password, OnAuthCompleteListener listener){
        if (validateInputs(name,email,mobile,password)){
            progressDialog.setMessage("Registering User...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            updateUserDatabase(name,mobile,password,"Email",listener);
                        }else {
                            listener.onFailure(task.getException());
                        }
                    });
        }
    }

    public void authenticateWithGoogle(String idToken, OnAuthCompleteListener listener){
        progressDialog.setMessage("Authenticating with Google...");
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getAdditionalUserInfo().isNewUser()){
                        updateUserDatabase(authResult.getUser().getDisplayName(), authResult.getUser().getPhoneNumber(),"","Google", listener);
                    }else {
                        progressDialog.dismiss();
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    listener.onFailure(e);
                });
    }

    private boolean validateInputs(String name, String email, String mobile, String password){
        if (TextUtils.isEmpty(name)){
            Toast.makeText(context,"Username is Required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(email)){
            Toast.makeText(context,"Email is Required!", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(mobile)){
            Toast.makeText(context,"Mobile Number is Required!", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password) || password.length() < 8){
            Toast.makeText(context,"Password must be >= Characters", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    protected void updateUserDatabase(String name, String phone, String password, String userType, OnAuthCompleteListener listener){
        progressDialog.setMessage("Saving User Info");
        progressDialog.show();

        long tileStamp = Utils.getTimestamp();
        String registerUserEmail = firebaseAuth.getCurrentUser().getEmail();
        String registerUserUid = firebaseAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name",name);
        hashMap.put("phoneNumber", phone);
        hashMap.put("profileImageUrl", " ");
        hashMap.put("userType", userType);
        hashMap.put("timestamp", tileStamp);
        hashMap.put("onlineStatus", true);
        hashMap.put("email", registerUserEmail);
        hashMap.put("uid", registerUserUid);
        hashMap.put("password", password);

        reference.child(registerUserUid)
                .setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    listener.onFailure(e);
                });
    }

    public interface OnAuthCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }
}
