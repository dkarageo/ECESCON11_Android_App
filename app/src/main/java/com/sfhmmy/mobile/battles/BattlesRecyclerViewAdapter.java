/*
 * BattlesRecyclerViewAdapter.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.battles;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class BattlesRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // List of battle posts to be displayed.
    private final List<BattlesPost> mBattlesPosts;

    private boolean mDisplayUnloggedUserItem;
    private boolean mMoreContentAfterLoadedContentExists;

    private LoginRequestListener mLoginRequestListener;


    private class PhotosVH extends RecyclerView.ViewHolder {

        ImageView mPhoto;
        TextView  mParticipationCode;

        PhotosVH(View v) {
            super(v);
            mPhoto             = v.findViewById(R.id.battles_photos_list_item_photo);
            mParticipationCode = v.findViewById(R.id.battles_photos_list_item_participation_code);
        }
    }

    private class UnloggedUserVH extends RecyclerView.ViewHolder {

        Button mLoginButton;

        UnloggedUserVH(View v) {
            super(v);
            mLoginButton = v.findViewById(R.id.battles_unlogged_user_notification_login_button);
        }
    }

    private class LoadingElementVH extends RecyclerView.ViewHolder {

        CircularProgressBar mProgressBar;

        LoadingElementVH(View v) {
            super(v);
            mProgressBar = v.findViewById(R.id.recycler_view_loading_item_progress_bar);
        }
    }


    BattlesRecyclerViewAdapter(List<BattlesPost> battlesPosts) {

        mBattlesPosts = new ArrayList<>();
        mBattlesPosts.addAll(battlesPosts);
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
                vh = new PhotosVH(v);
                break;

            case 1:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.battles_unlogged_user_notification_item_layout, viewGroup, false
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
        return mBattlesPosts.size();
    }

    void addBattlesPost(BattlesPost battlesPost) {
        synchronized (mBattlesPosts) {
            // When loading bar item is visible, push that to the end of the list before adding
            // the new image post.
            int insertPos = mMoreContentAfterLoadedContentExists ?
                    mBattlesPosts.size()-1 : mBattlesPosts.size();
            mBattlesPosts.add(insertPos, battlesPost);
            notifyItemRangeChanged(insertPos, mBattlesPosts.size()-insertPos);
        }
    }

    void clearBattlesPosts() {
        synchronized (mBattlesPosts) {
            mBattlesPosts.clear();

            // Fix placeholders for unlogged and loading bar items.
            if (mDisplayUnloggedUserItem) mBattlesPosts.add(new BattlesPost());
            if (mMoreContentAfterLoadedContentExists) mBattlesPosts.add(new BattlesPost());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mDisplayUnloggedUserItem && position == 0) return 1;
        if (mMoreContentAfterLoadedContentExists && position == mBattlesPosts.size()-1) return 2;
        else return 0;
    }

    void displayUnloggedUserItem(boolean display) {

        synchronized (mBattlesPosts) {
            // As long as notification should be visible, keep a null item to image posts list.
            if (display && !mDisplayUnloggedUserItem) mBattlesPosts.add(0, new BattlesPost());
            else if (!display && mDisplayUnloggedUserItem) mBattlesPosts.remove(0);

            mDisplayUnloggedUserItem = display;
        }

        notifyDataSetChanged();
    }

    void setMoreContentAfterLoadedContentExists(boolean moreContentAfterLoadedContentExists) {

        synchronized (mBattlesPosts) {
            if (moreContentAfterLoadedContentExists && !mMoreContentAfterLoadedContentExists) {
                // Add a placeholder item as the last item, to properly handle sizing.
                mBattlesPosts.add(new BattlesPost());
            } else if (!moreContentAfterLoadedContentExists && mMoreContentAfterLoadedContentExists) {
                mBattlesPosts.remove(mBattlesPosts.size() - 1);
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
        BattlesPost curPost = mBattlesPosts.get(position);

        holder.mParticipationCode.setText(Long.toString(curPost.getId()));

        Glide.with(holder.mPhoto.getContext())
                .load(curPost.getImageUrl())
                .placeholder(new ColorDrawable(App.getAppResources().getColor(R.color.white)))
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
