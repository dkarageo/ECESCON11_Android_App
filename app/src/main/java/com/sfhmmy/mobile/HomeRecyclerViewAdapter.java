/*
 * HomeRecyclerViewAdapter.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.2
 */

package com.sfhmmy.mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devs.readmoreoption.ReadMoreOption;
import com.sfhmmy.mobile.utils.DateTimeUtils;

import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class HomeRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // List of image posts to be displayed.
    private final List<ImagePost> mImagePosts;

    private boolean mDisplayUnloggedUserItem;
    private boolean mMoreContentAfterLoadedContentExists;

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

    private class LoadingElementVH extends RecyclerView.ViewHolder {

        CircularProgressBar mProgressBar;

        LoadingElementVH(View v) {
            super(v);
            mProgressBar = v.findViewById(R.id.recycler_view_loading_item_progress_bar);
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

            case 2:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.recycler_view_loading_item_layout, viewGroup, false
                );
                vh = new LoadingElementVH(v);
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

            case 2:
                // Nothing to bind for now.
                break;

            default:
                throw new RuntimeException("Invalid view type.");
        }
    }

    @Override
    public int getItemCount() {
        return mImagePosts.size();
    }

    void addImagePost(ImagePost imagePost) {
        synchronized (mImagePosts) {
            // When loading bar item is visible, push that to the end of the list before adding
            // the new image post.
            int insertPos = mMoreContentAfterLoadedContentExists ?
                    mImagePosts.size()-1 : mImagePosts.size();
            mImagePosts.add(insertPos, imagePost);
            notifyItemRangeChanged(insertPos, mImagePosts.size()-insertPos);
        }
    }

    void clearImagePosts() {
        synchronized (mImagePosts) {
            mImagePosts.clear();

            // Fix placeholders for unlogged and loading bar items.
            if (mDisplayUnloggedUserItem) mImagePosts.add(new ImagePost());
            if (mMoreContentAfterLoadedContentExists) mImagePosts.add(new ImagePost());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mDisplayUnloggedUserItem && position == 0) return 1;
        if (mMoreContentAfterLoadedContentExists && position == mImagePosts.size()-1) return 2;
        else return 0;
    }

    void displayUnloggedUserItem(boolean display) {

        synchronized (mImagePosts) {
            // As long as notification should be visible, keep a null item to image posts list.
            if (display && !mDisplayUnloggedUserItem) mImagePosts.add(0, new ImagePost());
            else if (!display && mDisplayUnloggedUserItem) mImagePosts.remove(0);

            mDisplayUnloggedUserItem = display;
        }

        notifyDataSetChanged();
    }

    void setMoreContentAfterLoadedContentExists(boolean moreContentAfterLoadedContentExists) {

        synchronized (mImagePosts) {
            if (moreContentAfterLoadedContentExists && !mMoreContentAfterLoadedContentExists) {
                // Add a placeholder item as the last item, to properly handle sizing.
                mImagePosts.add(new ImagePost());
            } else if (!moreContentAfterLoadedContentExists && mMoreContentAfterLoadedContentExists) {
                mImagePosts.remove(mImagePosts.size() - 1);
            }

            mMoreContentAfterLoadedContentExists = moreContentAfterLoadedContentExists;
        }

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
        holder.mUploadDate.setText(DateTimeUtils.getElapsedTimeInLocalizedText(
            curPost.getUploadedDate(), ZonedDateTime.now()
        ));

        ReadMoreOption readMoreOption = new ReadMoreOption.Builder(holder.mDescription.getContext())
                .textLength(80, ReadMoreOption.TYPE_CHARACTER)
                .moreLabel(App.getAppResources().getString(R.string.generic_prompt_for_expanding_text_view))
                .lessLabel(App.getAppResources().getString(R.string.generic_prompt_for_collapsing_text_view))
                .moreLabelColor(App.getAppResources().getColor(R.color.colorPrimaryDark))
                .lessLabelColor(App.getAppResources().getColor(R.color.colorPrimaryDark))
                .build();

        readMoreOption.addReadMoreTo(holder.mDescription, curPost.getDescription());

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
