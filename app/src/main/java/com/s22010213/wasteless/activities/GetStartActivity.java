package com.s22010213.wasteless.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.s22010213.wasteless.R;

public class GetStartActivity extends AppCompatActivity {

    Button startRegisterBtn, startLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_start);

        startLoginBtn = findViewById(R.id.start_login_button);
        startRegisterBtn = findViewById(R.id.start_register_button);

        startLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GetStartActivity.this, LoginActivity.class));
            }
        });

        startRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GetStartActivity.this, RegisterActivity.class));
            }
        });
    }
}