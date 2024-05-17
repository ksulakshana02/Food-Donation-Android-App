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
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.databinding.RowImageSliderBinding;
import com.s22010213.wasteless.models.ModelImageSlider;

import java.util.ArrayList;

public class AdapterImageSlider extends RecyclerView.Adapter<AdapterImageSlider.HolderImageSlider>{

    //View blinding
    private RowImageSliderBinding binding;
    private static final String TAG = "IMAGE_SLIDE_TAG";

    //context pf activity/fragment
    private Context context;

    //imageSliderArrayList the list of the images
    private ArrayList<ModelImageSlider> imageSliderArrayList;

    public AdapterImageSlider(Context context, ArrayList<ModelImageSlider> imageSliderArrayList) {
        this.context = context;
        this.imageSliderArrayList = imageSliderArrayList;
    }

    @NonNull
    @Override
    public HolderImageSlider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = RowImageSliderBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderImageSlider(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderImageSlider holder, int position) {
        //get data from particular position of list and
        ModelImageSlider modelImageSlider = imageSliderArrayList.get(position);

        //get url of the image
        String imageUrl = modelImageSlider.getImageUrl();
        String imageCount = (position + 1) + "/" + imageSliderArrayList.size();

        //set image counter
        holder.imageCount.setText(imageCount);

        try {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_new_image_grey)
                    .into(holder.imageView);

        }catch (Exception e){
            Log.d(TAG, "onBindViewHolder: ",e);
        }

        //handle image click, open in full screen
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public int getItemCount() {
        //return the size of list
        return imageSliderArrayList.size();
    }

    class HolderImageSlider extends RecyclerView.ViewHolder {

        //ui view of the row_image_slider.xml
        ShapeableImageView imageView;
        TextView imageCount;

        public HolderImageSlider(@NonNull View itemView) {
            super(itemView);

            imageView = binding.imageIv;
            imageCount = binding.imageCountTv;
        }
    }

}
