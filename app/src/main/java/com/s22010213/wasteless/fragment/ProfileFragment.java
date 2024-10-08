package com.s22010213.wasteless.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.s22010213.wasteless.activities.GetStartActivity;
import com.s22010213.wasteless.activities.MyListingsActivity;
import com.s22010213.wasteless.activities.ProfileEditActivity;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    Button logOutBtn;
    private FragmentProfileBinding binding;
    private Context mContext;
    private static final String TAG = "PROFILE_TAG";

    @Override
    public void onAttach(@NonNull Context context){
        mContext = context;
        super.onAttach(context);
    }

    public ProfileFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(LayoutInflater.from(mContext), container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        loadMyInfo();

        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });

        binding.profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ProfileEditActivity.class));
            }
        });

        binding.listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MyListingsActivity.class));
            }
        });

        binding.UserNearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMapFragment();
            }
        });
    }

    private void navigateToMapFragment() {
        Fragment mapFragment = new MapFragment();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, mapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    public void handleLogout(){
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    firebaseAuth.signOut();
                    Intent intent = new Intent(getActivity(), GetStartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
//                    startActivity(new Intent(getActivity(), GetStartActivity.class));
//                    getActivity().finish();
                }
            }
        });
    }

    private void loadMyInfo(){
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = "" + snapshot.child("email").getValue();
                        String name = ""+ snapshot.child("name").getValue();
                        String phone = ""+ snapshot.child("phoneNumber");
                        String profileImageUrl = "" + snapshot.child("profileImageUrl").getValue();

                        binding.profileUserName.setText(name);

                        try {
                            Glide.with(mContext)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ic_profile)
                                    .into(binding.profilePhoto);

                        }catch (Exception e){
                            Log.e(TAG,"onDataChange: ",e);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}