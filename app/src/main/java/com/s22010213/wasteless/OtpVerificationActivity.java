package com.s22010213.wasteless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private Button verifyBtn;
    private TextView resendText;
    private ProgressBar progressBar;
    private String verificationId;
    private String phoneNumber;

    private boolean resendEnable = false;
//    private int resendTime = 60;
//
//    private int selectedEtPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);

        resendText = findViewById(R.id.resend_text);
        verifyBtn = findViewById(R.id.otp_verify_btn);
        progressBar = findViewById(R.id.otp_progressBar);

        phoneNumber = getIntent().getStringExtra("mobile");
        verificationId = getIntent().getStringExtra("verificationId");

        if (phoneNumber != null){
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            signInWithCredential(phoneAuthCredential);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(OtpVerificationActivity.this,"Verification failed: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            verificationId = newVerificationId;
                            // Optional: Update UI to indicate OTP sent (not shown)
                        }
                    }
            );
        }

        setupOTPInputs();

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp1.getText().toString().trim().isEmpty()
                    || otp2.getText().toString().trim().isEmpty()
                    || otp3.getText().toString().trim().isEmpty()
                    || otp4.getText().toString().trim().isEmpty()
                    || otp5.getText().toString().trim().isEmpty()
                    || otp6.getText().toString().trim().isEmpty()) {
                    Toast.makeText(OtpVerificationActivity.this, "Please enter valid code", Toast.LENGTH_LONG).show();
                    return;
                }
                String code =
                        otp1.getText().toString() +
                                otp2.getText().toString() +
                                otp3.getText().toString() +
                                otp4.getText().toString() +
                                otp5.getText().toString() +
                                otp6.getText().toString();

                if (verificationId != null){
                    progressBar.setVisibility(View.VISIBLE);
                    verifyBtn.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId,
                            code
                    );
                    signInWithCredential(phoneAuthCredential);

//                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    if (task.isSuccessful()){
//                                        progressBar.setVisibility(View.GONE);
//                                        Toast.makeText(OtpVerificationActivity.this, "Verification successful!", Toast.LENGTH_SHORT).show();
//                                        startActivity(new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class));
//
//                                    }else {
//                                        progressBar.setVisibility(View.GONE);
//                                        verifyBtn.setVisibility(View.VISIBLE);
//                                        Toast.makeText(OtpVerificationActivity.this, "Invalid code!", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
                }
            }
        });

        resendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificationId != null){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(OtpVerificationActivity.this,"OTP code resent", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential){
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(OtpVerificationActivity.this, "Verification successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class));
                        }else {
                            progressBar.setVisibility(View.GONE);
                            verifyBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(OtpVerificationActivity.this, "Invalid code!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setupOTPInputs() {
        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    otp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    otp6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


//        otp1.addTextChangedListener(textWatcher);
//        otp2.addTextChangedListener(textWatcher);
//        otp3.addTextChangedListener(textWatcher);
//        otp4.addTextChangedListener(textWatcher);
//        otp5.addTextChangedListener(textWatcher);


//        showKeyboard(otp1);
//
//        startCountDownTimer();

//        resendText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (resendEnable){
//                    startCountDownTimer();
//                }
//            }
//        });

//        verifyBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String generateOtp = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString() + otp5.getText().toString();
//
//                if (generateOtp.length() == 5){
//
//                }
//
//            }
//        });



//    private void showKeyboard(EditText otp){
//        otp.requestFocus();
//
//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.showSoftInput(otp, InputMethodManager.SHOW_IMPLICIT);
//    }

//    private void startCountDownTimer(){
//
//        resendEnable = false;
//        resendText.setTextColor(Color.parseColor("#99000000"));
//
//        new CountDownTimer(resendTime*60, 100) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                resendText.setText("Resend Code (" + (millisUntilFinished / 60) + ")");
//            }
//
//            @Override
//            public void onFinish() {
//                resendEnable = true;
//                resendText.setText("Resend Code");
//            }
//        }.start();
//    }

//    private final TextWatcher textWatcher = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            if (s.length() > 0){
//                if (selectedEtPosition == 0){
//                    selectedEtPosition = 1;
//                    showKeyboard(otp2);
//                } else if (selectedEtPosition == 1) {
//                    selectedEtPosition = 2;
//                    showKeyboard(otp3);
//                } else if (selectedEtPosition ==2) {
//                    selectedEtPosition = 3;
//                    showKeyboard(otp4);
//                } else if (selectedEtPosition == 3) {
//                    selectedEtPosition = 4;
//                    showKeyboard(otp5);
//                }
//            }
//        }
//    };


//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event){
//
//        if (keyCode == KeyEvent.KEYCODE_DEL){
//            if (selectedEtPosition == 4){
//                selectedEtPosition = 3;
//                showKeyboard(otp4);
//
//            } else if (selectedEtPosition == 3) {
//                selectedEtPosition = 2;
//                showKeyboard(otp3);
//
//            } else if (selectedEtPosition == 2) {
//                selectedEtPosition = 1;
//                showKeyboard(otp2);
//
//            } else if (selectedEtPosition == 1) {
//                selectedEtPosition = 0;
//                showKeyboard(otp1);
//            }
//            return true;
//        }
//        else {
//            return super.onKeyUp(keyCode, event);
//        }
//    }
}