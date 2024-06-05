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
import com.s22010213.wasteless.AuthManager;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.Utils;
import com.s22010213.wasteless.databinding.ActivityRegisterBinding;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private static final String TAG = "REGISTER_ACTIVITY_TAG";
    private EditText registerName, registerEmail,registerMobile, registerPassword;
    private ImageView googleBtn;
    private TextView loginRedirectText;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private AuthManager authManager;


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
        authManager = new AuthManager(this);

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

        googleBtn.setOnClickListener( v -> beginGoogleLogin());


        registerButton.setOnClickListener(v -> {
                String name = registerName.getText().toString().trim();
                String email = registerEmail.getText().toString().trim();
                String mobile = registerMobile.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();

                authManager.registerUserWithEmail(name, email, mobile, password, new AuthManager.OnAuthCompleteListener() {
                    @Override
                    public void onSuccess() {
                        Utils.toast(RegisterActivity.this,"You have signed up successfully!");
                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        finishAffinity();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Utils.toast(RegisterActivity.this,"Error! "+ e.getMessage());
                    }
                });
        });

        loginRedirectText.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    private void beginGoogleLogin() {
        Intent googleSignIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInARL.launch(googleSignIntent);
    }

    private ActivityResultLauncher<Intent> googleSignInARL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                    Log.d(TAG,"onActivityResult: ");

                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                        try {
                            //google sign in was successful, authentication with firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG,"onActivityResult: Account ID: "+ account.getId());
                            authManager.authenticateWithGoogle(account.getIdToken(), new AuthManager.OnAuthCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                    finishAffinity();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Utils.toast(RegisterActivity.this, "Authentication Failed: "+ e.getMessage());
                                }
                            });
                        }catch (ApiException e){
                            Log.e(TAG,"onActivityResult: ",e);
                            Utils.toast(RegisterActivity.this, "Google Sign-In Failed: "+ e.getMessage());
                        }
                    }else {
                        //cancelled from google signIn option
                        Log.d(TAG,"onActivityResult: Cancelled");
                        Utils.toast(RegisterActivity.this,"Google Sign-In Cancelled");
                    }
            }
    );
}