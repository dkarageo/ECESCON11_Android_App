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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sfhmmy.mobile.utils.DateTimeUtils;

import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class HomeRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // List of image posts to be displayed.
    private List<ImagePost> mImagePosts;

    private boolean mDisplayUnloggedUserItem;

    private LoginRequestListener mLoginRequestListener;


    private class PhotosVH extends RecyclerView.ViewHolder {

        ImageView mPhoto;
        TextView mPhotoError;
        TextView mUploader;
        TextView mUploadDate;
        TextView mDescription;

        PhotosVH(View v) {
            super(v);
            mPhoto = v.findViewById(R.id.home_photos_list_item_photo);
            mPhotoError = v.findViewById(R.id.home_photos_list_item_photo_load_error);
            mUploader = v.findViewById(R.id.home_photos_list_item_uploader);
            mUploadDate = v.findViewById(R.id.home_photos_list_item_uploaded_date);
            mDescription = v.findViewById(R.id.home_photos_list_item_description);
        }
    }

    private class UnloggedUserVH extends RecyclerView.ViewHolder {

        Button mLoginButton;

        UnloggedUserVH(View v) {
            super(v);
            mLoginButton = v.findViewById(R.id.home_unlogged_user_notification_login_button);
        }
    }


    HomeRecyclerViewAdapter(List<ImagePost> imagePosts) {

        mImagePosts = new ArrayList<>();
        mImagePosts.addAll(imagePosts);
    }

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder vh;
        View v;

        switch(viewType) {
            case 0:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.home_photo_item_layout, viewGroup, false
                );
                vh = new PhotosVH(v);
                break;

            case 1:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.home_unlogged_user_notification_item_layout, viewGroup, false
                );
                vh = new UnloggedUserVH(v);
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

            case 1:
                bindUnloggedUserVH((UnloggedUserVH) holder, position);
                break;

            default:
                throw new RuntimeException("Invalid view type.");
        }
    }

    @Override
    public int getItemCount() {
        return mImagePosts.size();
    }

    void updateImagePosts(List<ImagePost> imagePosts) {
        mImagePosts.clear();
        mImagePosts.addAll(imagePosts);
        if (mDisplayUnloggedUserItem) mImagePosts.add(0, new ImagePost());
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mDisplayUnloggedUserItem && position == 0) return 1;
        else return 0;
    }

    void displayUnloggedUserItem(boolean display) {
        // As long as notification should be visible, keep a null item to image posts list.
        if (display && !mDisplayUnloggedUserItem) mImagePosts.add(0, new ImagePost());
        else if (!display && mDisplayUnloggedUserItem) mImagePosts.remove(0);

        mDisplayUnloggedUserItem = display;

        notifyDataSetChanged();
    }

    public void setLoginRequestListener(LoginRequestListener l) {
        mLoginRequestListener = l;
    }

    private void notifyOnLoginRequest() {
        mLoginRequestListener.onLoginRequest();
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

    private void bindUnloggedUserVH(UnloggedUserVH holder, int position) {
        holder.mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyOnLoginRequest();
            }
        });
    }


    public interface LoginRequestListener {
        void onLoginRequest();
    }
}
