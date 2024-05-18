package com.s22010213.wasteless.adapters;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.Utils;
import com.s22010213.wasteless.databinding.RowImagesPickedBinding;
import com.s22010213.wasteless.models.ModelImagePicked;

import java.util.ArrayList;

public class AdapterImagesPicked extends RecyclerView.Adapter<AdapterImagesPicked.HolderImagesPicked> {

    private RowImagesPickedBinding binding;
    private static final String TAG = "IMAGES_TAG";

    private Context context;
    private ArrayList<ModelImagePicked> imagePickedArrayList;
    private String adId;

    public AdapterImagesPicked(Context context, ArrayList<ModelImagePicked> imagePickedArrayList, String adId) {
        this.context = context;
        this.imagePickedArrayList = imagePickedArrayList;
        this.adId = adId;
    }

    @NonNull
    @Override
    public HolderImagesPicked onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = RowImagesPickedBinding.inflate(LayoutInflater.from(context),parent, false);
        return new HolderImagesPicked(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderImagesPicked holder, int position) {
        //get data from particular position
        ModelImagePicked model = imagePickedArrayList.get(position);

        if (model.getFromInternet()){
            //image is from internet
            String imageUrl = model.getImageUrl();

            Log.d(TAG, "onBindViewHolder: imageUri: "+ imageUrl);
            //set the image
            try{
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_image_grey)
                        .into(holder.image);
            } catch (Exception e){
                Log.e(TAG, "onBindViewHolder: "+ e);
            }
        }else {

            //image is picked from gallery
            Uri imageUri = model.getImageUri();

            try{
                Glide.with(context)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_image_grey)
                        .into(holder.image);
            } catch (Exception e){
                Log.e(TAG, "onBindViewHolder: "+ e);
            }
        }


        //handle close btn
        holder.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (model.getFromInternet()){
                    deleteImageFirebase(model,holder,position);

                }else {
                    imagePickedArrayList.remove(model);
                    notifyItemRemoved(position);
                }
            }
        });
    }

    private void deleteImageFirebase(ModelImagePicked model, HolderImagesPicked holder, int position) {
        //id of the image to delete image
        String imageId = model.getId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId).child("Images").child(imageId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Utils.toast(context,"Image Deleted!");

                        try {
                            //remove from imagePickedArrayList
                            imagePickedArrayList.remove(model);
                            notifyItemRemoved(position);

                        } catch (Exception e){
                            Log.e(TAG,"OnSuccess: ",e);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Delete failure
                        Utils.toast(context,"Failed to delete image due to "+e.getMessage());
                    }
                });

    }


    @Override
    public int getItemCount() {
        //return the size of list
        return imagePickedArrayList.size();
    }

    class HolderImagesPicked extends RecyclerView.ViewHolder{

        ImageView image;
        ImageButton closeBtn;

        public HolderImagesPicked(@NonNull View itemView){
            super(itemView);

            image = binding.image;
            closeBtn = binding.closeBtn;

        }
    }
}
