package com.s22010213.wasteless;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4, otp5;
    private Button verifyBtn;
    private TextView resendText;

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

        resendText = findViewById(R.id.resend_text);
        verifyBtn = findViewById(R.id.otp_verify_btn);


        setupOTPInputs();

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

    }

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