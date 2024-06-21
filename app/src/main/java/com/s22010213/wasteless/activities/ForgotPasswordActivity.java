package com.s22010213.wasteless.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.s22010213.wasteless.JavaMailSender;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.Utils;
import com.s22010213.wasteless.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private Button sendCodeBtn;
    private EditText forgotPwdEmail;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        sendCodeBtn = findViewById(R.id.send_code_button);
        forgotPwdEmail = findViewById(R.id.email_for_forgot_password);
        progressBar = findViewById(R.id.forgot_progressBar);
        firebaseAuth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.GONE);

//        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              onBackPressed();
//            }
//        });

        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String recipientEmail = forgotPwdEmail.getText().toString().trim();

                if (recipientEmail.isEmpty()){
                    Utils.toast(ForgotPasswordActivity.this , "Enter Email");
                    return;
                }

                String otp = Utils.generateOtp();
                Log.d("otp", otp);
//                String otp = "123456";
                progressBar.setVisibility(View.VISIBLE);
                sendCodeBtn.setVisibility(View.GONE);

                JavaMailSender.sendOtpEmail(recipientEmail, otp, new JavaMailSender.EmailSentCallback() {
                    @Override
                    public void onEmailSent() {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            sendCodeBtn.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class);
                            intent.putExtra("otp",otp);
                            intent.putExtra("email",recipientEmail);
                            startActivity(intent);
                        });
                    }

                    @Override
                    public void onEmailFailed(Exception e) {
                        e.printStackTrace();
                        runOnUiThread(()->{
                            progressBar.setVisibility(View.GONE);
                            sendCodeBtn.setVisibility(View.VISIBLE);
                            Utils.toast(ForgotPasswordActivity.this,"Failed to sent OTP");
                        });
                    }
                }
            );


//                PhoneAuthOptions options =
//                        PhoneAuthOptions.newBuilder(firebaseAuth)
//                                .setPhoneNumber(phone)
//                                        .setTimeout(60L, TimeUnit.SECONDS)
//                                .setActivity(ForgotPasswordActivity.this)
//                                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                                    @Override
//                                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                        sendCodeBtn.setVisibility(View.VISIBLE);
//                                        progressBar.setVisibility(View.GONE);
//                                        Intent intent = new Intent(getApplicationContext(), OtpVerificationActivity.class);
//                                        intent.putExtra("mobile", forgotPwdMobile.getText().toString());
//                                        intent.putExtra("verificationId",verificationId);
//                                        startActivity(intent);
//                                    }
//                                    @Override
//                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                        sendCodeBtn.setVisibility(View.VISIBLE);
//                                        progressBar.setVisibility(View.GONE);
//                                    }
//
//                                    @Override
//                                    public void onVerificationFailed(@NonNull FirebaseException e) {
//                                        progressBar.setVisibility(View.GONE);
//                                        sendCodeBtn.setVisibility(View.VISIBLE);
//                                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                                    .build();
//                        PhoneAuthProvider.verifyPhoneNumber(options);

//                firebaseAuth.sendPasswordResetEmail(phone)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()){
//                                    Toast.makeText(ForgotPasswordActivity.this , "Email sent", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                firebaseAuth = FirebaseAuth.getInstance();


//                PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                         phone,
//                        60,
//                        TimeUnit.SECONDS,
//                        ForgotPasswordActivity.this,
//                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
//
//                            @Override
//                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                sendCodeBtn.setVisibility(View.VISIBLE);
//                            }
//
//                            @Override
//                            public void onVerificationFailed(@NonNull FirebaseException e) {
//                                sendCodeBtn.setVisibility(View.VISIBLE);
//                                Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                sendCodeBtn.setVisibility(View.VISIBLE);
//                                Intent intent = new Intent(getApplicationContext(), OtpVerificationActivity.class);
//                                intent.putExtra("mobile", forgotPwdMobile.getText().toString());
//                                intent.putExtra("verificationId",verificationId);
//                                startActivity(intent);
//                            }
//                        }
//                );
            }
        });
    }
}