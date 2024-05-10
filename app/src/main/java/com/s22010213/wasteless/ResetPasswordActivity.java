package com.s22010213.wasteless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText newPasswordEt, confirmNewPasswordEt;
    Button resetPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordEt = findViewById(R.id.new_password);
        confirmNewPasswordEt = findViewById(R.id.confirm_new_password);
        resetPasswordBtn = findViewById(R.id.reset_button);

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordEt.getText().toString().trim();
                String confirmNewPassword = confirmNewPasswordEt.getText().toString().trim();

                if (!validatePassword(newPassword, confirmNewPassword)){
                    return;
                }

                resetPassword(newPassword);
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

    private void resetPassword(String newPassword){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            user.updatePassword(newPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                finish();
                            }else {
                                Toast.makeText(ResetPasswordActivity.this, "Password reset failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } else {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}