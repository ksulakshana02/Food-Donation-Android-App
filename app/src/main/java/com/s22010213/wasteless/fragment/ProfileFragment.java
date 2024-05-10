package com.s22010213.wasteless.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.s22010213.wasteless.GetStartActivity;
import com.s22010213.wasteless.LoginActivity;
import com.s22010213.wasteless.R;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    Button logOutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        logOutBtn = view.findViewById(R.id.logout_btn);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }


    public void handleLogout(){
        firebaseAuth.signOut();
        startActivity(new Intent(getActivity(), GetStartActivity.class));
    }


}