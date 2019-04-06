package com.sfhmmy.mobile.battles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sfhmmy.mobile.battles.BattlesPost;
import com.sfhmmy.mobile.utils.DateTimeUtils;

import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BattlesRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // List of battle posts to be displayed.
    private List<BattlesPost> mBattlePosts;

    private boolean mDisplayUnloggedUserItem;

    private LoginRequestListener mLoginRequestListener;


    private class PhotosVH extends RecyclerView.ViewHolder {

        ImageView mPhoto;
        TextView mPhotoError;
        TextView mUploadDate;
        TextView mDescription;

        PhotosVH(View v) {
            super(v);
            mPhoto = v.findViewById(R.id.battles_photos_list_item_photo);
            mPhotoError = v.findViewById(R.id.battles_photos_list_item_photo_load_error);
            mUploadDate = v.findViewById(R.id.battles_photos_list_item_uploaded_date);
            mDescription = v.findViewById(R.id.battles_photos_list_item_description);

        }
    }

    private class UnloggedUserVH extends RecyclerView.ViewHolder {

        Button mLoginButton;

        UnloggedUserVH(View v) {
            super(v);
            mLoginButton = v.findViewById(R.id.home_unlogged_user_notification_login_button);
        }
    }


    BattlesRecyclerViewAdapter(List<BattlesPost> BattlePosts) {

        mBattlePosts = new ArrayList<>();
        mBattlePosts.addAll(BattlePosts);
    }

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder vh;
        View v;

        switch(viewType) {
            case 0:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.battles_photo_item_layout, viewGroup, false
                );
                vh = new BattlesRecyclerViewAdapter.PhotosVH(v);
                break;

            case 1:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.battles_unlogged_user_notification_item_layout, viewGroup, false
                );
                vh = new BattlesRecyclerViewAdapter.UnloggedUserVH(v);
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
        return mBattlePosts.size();
    }

    void updateBattlePosts(List<BattlesPost> BattlesPosts) {
        mBattlePosts.clear();
        mBattlePosts.addAll(BattlesPosts);
        if (mDisplayUnloggedUserItem) mBattlePosts.add(0, new BattlesPost());
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mDisplayUnloggedUserItem && position == 0) return 1;
        else return 0;
    }

    void displayUnloggedUserItem(boolean display) {
        // As long as notification should be visible, keep a null item to image posts list.
        if (display && !mDisplayUnloggedUserItem) mBattlePosts.add(0, new BattlesPost());
        else if (!display && mDisplayUnloggedUserItem) mBattlePosts.remove(0);

        mDisplayUnloggedUserItem = display;

        notifyDataSetChanged();
    }

    public void setLoginRequestListener(LoginRequestListener l) {
        mLoginRequestListener = l;
    }

    private void notifyOnLoginRequest() {
        mLoginRequestListener.onLoginRequest();
    }

    private void bindPhotosVH(BattlesRecyclerViewAdapter.PhotosVH holder, int position) {
        BattlesPost curPost = mBattlePosts.get(position);

        holder.mDescription.setText(curPost.getDescription());
        holder.mUploadDate.setText(DateTimeUtils.getElapsedTimeInLocalizedText(
                curPost.getUploadedDate(), ZonedDateTime.now()
        ));

        Glide.with(holder.mPhoto.getContext())
                .load(curPost.getImageUrl())
                .fitCenter()
                .into(holder.mPhoto);
    }

    private void bindUnloggedUserVH(BattlesRecyclerViewAdapter.UnloggedUserVH holder, int position) {
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