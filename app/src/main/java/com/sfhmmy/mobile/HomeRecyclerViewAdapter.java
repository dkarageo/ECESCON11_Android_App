/*
 * HomeRecyclerViewAdapter.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sfhmmy.mobile.utils.DateTimeUtils;

import org.threeten.bp.ZonedDateTime;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class HomeRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // List of image posts to be displayed.
    private List<ImagePost> mImagePosts;


    private class PhotosVH extends RecyclerView.ViewHolder {

        ImageView mPhoto;
        TextView mPhotoError;
        TextView mUploader;
        TextView mUploadDate;
        TextView mDescription;

        public PhotosVH(View v) {
            super(v);
            mPhoto = v.findViewById(R.id.home_photos_list_item_photo);
            mPhotoError = v.findViewById(R.id.home_photos_list_item_photo_load_error);
            mUploader = v.findViewById(R.id.home_photos_list_item_uploader);
            mUploadDate = v.findViewById(R.id.home_photos_list_item_uploaded_date);
            mDescription = v.findViewById(R.id.home_photos_list_item_description);
        }
    }


    public HomeRecyclerViewAdapter(List<ImagePost> imagePosts) {
        mImagePosts = imagePosts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder vh;

        switch(viewType) {
            case 0:
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.home_photo_item_layout, viewGroup, false
                );
                vh = new PhotosVH(v);
                break;

            default:
                throw new RuntimeException("Invalid view type.");
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()) {
            case 0:
                bindPhotosVH((PhotosVH) holder, position);
                break;

            default:
                throw new RuntimeException("Invalid view type.");
        }
    }

    @Override
    public int getItemCount() {
        return mImagePosts.size();
    }

    public void updateImagePosts(List<ImagePost> imagePosts) {
        mImagePosts = imagePosts;
        notifyDataSetChanged();
    }

    private void bindPhotosVH(PhotosVH holder, int position) {
        ImagePost curPost = mImagePosts.get(position);

        holder.mUploader.setText(curPost.getUploader());
        holder.mDescription.setText(curPost.getDescription());
        holder.mUploadDate.setText(DateTimeUtils.getElapsedTimeInLocalizedText(
            curPost.getUploadedDate(), ZonedDateTime.now()
        ));

        Glide.with(holder.mPhoto.getContext())
                .load(curPost.getImageUrl())
                .fitCenter()
                .into(holder.mPhoto);
    }
}
