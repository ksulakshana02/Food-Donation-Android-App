package com.s22010213.wasteless.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.s22010213.wasteless.FilterAd;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.Utils;
import com.s22010213.wasteless.databinding.RowAdBinding;
import com.s22010213.wasteless.models.ModelAd;

import java.util.ArrayList;

public class AdapterAd extends RecyclerView.Adapter<AdapterAd.HolderAd> {

    private Context context;
    public ArrayList<ModelAd> adArrayList;
    private ArrayList<ModelAd> filterList;
    private FilterAd filer;
    private RowAdBinding binding;
    private static final String TAG = "ADAPTER_AD_TAG";
    private FirebaseAuth firebaseAuth;

    public AdapterAd(Context context, ArrayList<ModelAd> adArrayList){
        this.context = context;
        this.adArrayList = adArrayList;
        this.filterList = adArrayList;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderAd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowAdBinding.inflate(LayoutInflater.from(context), parent,false);
        return new HolderAd(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAd holder, int position) {
        ModelAd modelAd = adArrayList.get(position);

        String title = modelAd.getTitle();
        String description = modelAd.getDescription();
        String quantity = modelAd.getQuantity();
        String foodType = modelAd.getFood_type();
        long timestamp = modelAd.getTimestamp();
        String formattedDate = Utils.formatTimestampDate(timestamp);

        loadFirstImage(modelAd, holder);

        holder.title.setText(title);
        holder.description.setText(description);
        holder.quantity.setText(quantity + " Available");
        holder.condition.setText(foodType);
        holder.date.setText(formattedDate);
    }

    private void loadFirstImage(ModelAd modelAd, HolderAd holder){
        Log.d(TAG,"loadAdFirstImage: ");

        String adId = modelAd.getId();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ads");
        reference.child(adId).child("Images").limitToFirst(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                             String imageUrl = ""+ ds.child("imageUrl").getValue();
                             Log.d(TAG,"onDataChange: imageUrl: "+imageUrl);

                             try{
                                 Glide.with(context)
                                         .load(imageUrl)
                                         .placeholder(R.drawable.ic_new_image_grey)
                                         .into(holder.imageIv);

                             }catch (Exception e){
                                 Log.e(TAG,"onDataChange: ",e);
                             }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return adArrayList.size();
    }

//    @Override
//    public Filter getFilter() {
//        if (filer == null){
//            filer = new FilterAd(this, filterList);
//        }
//        return filer;
//    }

    class HolderAd extends RecyclerView.ViewHolder{

        ShapeableImageView imageIv;
        TextView title, description, quantity, condition, date;


        public HolderAd(@NonNull View itemView) {
            super(itemView);

            imageIv = binding.imageIv;
            title = binding.title;
            description = binding.description;
            quantity = binding.availableQuantity;
            condition = binding.foodType;
            date = binding.date;
        }
    }
}
