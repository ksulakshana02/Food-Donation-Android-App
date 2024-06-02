package com.s22010213.wasteless.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.Utils;
import com.s22010213.wasteless.databinding.ActivityRegisterBinding;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private static final String TAG = "REGISTER_ACTIVITY_TAG";
    EditText registerName, registerEmail,registerMobile, registerPassword;
    ImageView googleBtn;
    TextView loginRedirectText;
    Button registerButton;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    DatabaseReference reference;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerName = findViewById(R.id.register_username);
        registerEmail = findViewById(R.id.register_email);
        registerMobile = findViewById(R.id.register_mobile);
        registerPassword = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register_button);
        loginRedirectText = findViewById(R.id.already_registered);
        googleBtn = findViewById(R.id.googleIcon);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        //configure google sign-in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);


//        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginGoogleLogin();
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = registerName.getText().toString().trim();
                String email = registerEmail.getText().toString().trim();
                String mobile = registerMobile.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();
                //check input validation
                if (TextUtils.isEmpty(name)){
                    registerName.setError("Username is Required!");
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    registerEmail.setError("Email is Required!");
                    return;
                }

                if (TextUtils.isEmpty(mobile)){
                    registerMobile.setError("Mobile Number is Required!");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    registerPassword.setError("Password is Required!");
                    return;
                }
                if (password.length() < 8){
                    registerPassword.setError("Password must be >= 8 Characters");
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            Toast.makeText(RegisterActivity.this, "You have signup successfully!", Toast.LENGTH_SHORT).show();
                            updateUserInfo();
                        }else {
                            Toast.makeText(RegisterActivity.this,"Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void beginGoogleLogin() {
        Intent googleSignIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInARL.launch(googleSignIntent);
    }

    private ActivityResultLauncher<Intent> googleSignInARL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG,"onActivityResult: ");

                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                        try {
                            //google sign in was successful, authentication with firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG,"onActivityResult: Account ID: "+ account.getId());
                            firebaseAuthWithGoogleAccount(account.getIdToken());

                        }catch (ApiException e){
                            Log.e(TAG,"onActivityResult: ",e);
                        }
                    }else {
                        //cancelled from google signIn option
                        Log.d(TAG,"onActivityResult: Cancelled");
                        Utils.toast(RegisterActivity.this,"Cancelled...");
                    }
                }
            }
    );

    private void firebaseAuthWithGoogleAccount(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            updateUserInfoDd();
                        }else {

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void updateUserInfoDd(){
        progressDialog.setMessage("Saving User Info");
        progressDialog.show();

        long timeStamp = Utils.getTimestamp();
        String registerUserEmail = firebaseAuth.getCurrentUser().getEmail();
        String registerUserUid = firebaseAuth.getUid();
        String name = firebaseAuth.getCurrentUser().getDisplayName();
        String number = firebaseAuth.getCurrentUser().getPhoneNumber();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("phoneNumber", number);
        hashMap.put("profileImageUrl", " ");
        hashMap.put("userType", "Google");
        hashMap.put("timestamp", timeStamp);
        hashMap.put("onlineStatus", true);
        hashMap.put("email", registerUserEmail);
        hashMap.put("uid", registerUserUid);
        hashMap.put("password", "");

        //set data to firebase db
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(registerUserUid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //firebase fb save success
                        progressDialog.dismiss();

                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //firebase database save failed
                        progressDialog.dismiss();
                        Utils.toast(RegisterActivity.this, "Failed to save info due to "+ e.getMessage());
                    }
                });

    }

    private void updateUserInfo(){
        progressDialog.setMessage("Saving User Info");
        progressDialog.show();

        long timeStamp = Utils.getTimestamp();
        String registerUserEmail = firebaseAuth.getCurrentUser().getEmail();
        String registerUserUid = firebaseAuth.getUid();
        String password = registerPassword.getText().toString().trim();
        String name = registerName.getText().toString().trim();
        String mobile = registerMobile.getText().toString().trim();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("phoneNumber", mobile);
        hashMap.put("profileImageUrl", " ");
        hashMap.put("userType", "Email");
        hashMap.put("timestamp", timeStamp);
        hashMap.put("onlineStatus", true);
        hashMap.put("email", registerUserEmail);
        hashMap.put("uid", registerUserUid);
        hashMap.put("password", password);

        //set data to firebase db
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(registerUserUid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //firebase fb save success
                        progressDialog.dismiss();

                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //firebase database save failed
                        progressDialog.dismiss();
                        Utils.toast(RegisterActivity.this, "Failed to save info due to "+ e.getMessage());
                    }
                });

    }

}