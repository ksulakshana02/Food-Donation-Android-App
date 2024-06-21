package com.s22010213.wasteless.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.Utils;
import com.s22010213.wasteless.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;
    EditText newPasswordEt, confirmNewPasswordEt;
    Button resetPasswordBtn;
    ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordEt = findViewById(R.id.new_password);
        confirmNewPasswordEt = findViewById(R.id.confirm_new_password);
        resetPasswordBtn = findViewById(R.id.reset_button);
        progressBar = findViewById(R.id.reset_progressBar);
        firebaseAuth = FirebaseAuth.getInstance();

        String email = getIntent().getStringExtra("email");
        Log.d("resetEmail",email);

        progressBar.setVisibility(View.GONE);

//        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordEt.getText().toString().trim();
                String confirmNewPassword = confirmNewPasswordEt.getText().toString().trim();

                if (validatePassword(newPassword, confirmNewPassword)){
                    progressBar.setVisibility(View.VISIBLE);
                    resetPasswordBtn.setVisibility(View.GONE);

                    resetPassword(newPassword, email);
//                    return;
                }

//                progressBar.setVisibility(View.VISIBLE);
//                resetPasswordBtn.setVisibility(View.GONE);
//
//                resetPassword(newPassword, email);
            }
        });
    }

    private boolean validatePassword(String newPassword, String confirmNewPassword){
        if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "Please enter new password and confirmation", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPassword.length() < 8){
            Toast.makeText(this, "Please must be at least 8 characters long", Toast.LENGTH_SHORT).show();
        }

        if (!newPassword.equals(confirmNewPassword)){
            Toast.makeText(this,"Password do not match", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private void resetPassword(String newPassword, String email){
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null){
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        resetPasswordBtn.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        if (updateTask.isSuccessful()){
                                            Utils.toast(ResetPasswordActivity.this,"Password reset successfully");
                                            startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                            finish();
                                        }else {
                                            Utils.toast(ResetPasswordActivity.this,"Failed to reset password");
                                        }
                                    });
                        }else {
                            resetPasswordBtn.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            Utils.toast(ResetPasswordActivity.this,"Failed to fetch sign-in methods");
                        }
                    }
                });

    }
}