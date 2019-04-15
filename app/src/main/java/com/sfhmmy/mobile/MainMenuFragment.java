/*
 * MainMenuFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import jp.wasabeef.glide.transformations.BlurTransformation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.sfhmmy.mobile.about.AboutFragment;
import com.sfhmmy.mobile.promo.InfoFragment;
import com.sfhmmy.mobile.settings.SettingsFragment;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.users.UserManager;
import com.sfhmmy.mobile.users.UserProfileFragment;
import com.sfhmmy.mobile.utils.DrawableUtils;


public class MainMenuFragment extends UserAwareFragment {

    private TopLevelFragmentEventsListener mTopListener;

    // Header handlers
    private TextView mHeaderUserName;
    private TextView mHeaderUserRole;
    private View mHeaderWrapper;
    private NestedScrollView mScrollView;
    private ImageView mHeaderBackground;
    private ImageView mHeaderProfileImage;

    // Items list handlers
    private ExpandableHeightListView mItemsList;

    private boolean mIsHeaderExpanded;


    public MainMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TopLevelFragmentEventsListener) {
            mTopListener = (TopLevelFragmentEventsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TopLevelFragmentEventsListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main_menu, container, false);

        // Fetch header handlers.
        mHeaderUserName = root.findViewById(R.id.main_menu_header_user_name);
        mHeaderUserRole = root.findViewById(R.id.main_menu_header_user_role);
        mHeaderWrapper = root.findViewById(R.id.main_menu_header_wrapper);
        mScrollView = root.findViewById(R.id.main_menu_root_view);
        mHeaderBackground = root.findViewById(R.id.main_menu_header_bg);
        mHeaderProfileImage = root.findViewById(R.id.main_menu_header_profile_img);

        // Fetch items list handlers.
        mItemsList = root.findViewById(R.id.main_menu_items_list);

        mHeaderWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On click of the header, expand and shrink the header.
                setHeaderExpansion(!mIsHeaderExpanded);
            }
        });

        mItemsList.setExpanded(true);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mTopListener != null) {
            mTopListener.updateTitle(getString(R.string.profile_menu_tile));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTopListener = null;
    }

    @Override
    protected void onCreateUserSpecificContent(User user) {
        super.onCreateUserSpecificContent(user);

        if (user != null) displayUserMenu(user);
        else displayGenericMenu();
    }

    private void setHeaderExpansion(boolean expand) {
        if (expand && !mIsHeaderExpanded) {
            mHeaderUserName.setVisibility(
                    mHeaderUserName.getText().length() > 0 ? View.VISIBLE : View.GONE);
            mHeaderUserRole.setVisibility(
                    mHeaderUserRole.getText().length() > 0 ? View.VISIBLE : View.GONE);
            mIsHeaderExpanded = true;
        } else if (!expand && mIsHeaderExpanded) {
            mHeaderUserName.setVisibility(View.GONE);
            mHeaderUserRole.setVisibility(View.GONE);
            mIsHeaderExpanded = false;
        }
    }

    private void displayUserMenu(@NonNull User user) {
        String fullName = String.format("%s %s", user.getName(), user.getSurname());

        mHeaderUserName.setText(fullName);
        mHeaderUserRole.setText(user.getRoleText());

        Glide.with(mHeaderBackground)
             .load(user.getProfilePictureURL())
             .transform(new BlurTransformation(25, 3))
             .into(mHeaderBackground);

        Glide.with(mHeaderProfileImage)
                .load(user.getProfilePictureURL())
                .into(mHeaderProfileImage);

        createMenuWithUserSpecificOptions(user);

        // Display user's name on action bar too.
        if (mTopListener != null) mTopListener.updateTitle(fullName);
    }

    private void displayGenericMenu() {

        mHeaderUserName.setText(getString(R.string.main_menu_header_generic_user_name));
        mHeaderUserRole.setText(null);
        mHeaderUserRole.setVisibility(View.GONE);

        Glide.with(mHeaderBackground)
                .load("https://www.gravatar.com/avatar/asdfasdfsd?d=retro")
                .transform(new BlurTransformation(25, 3))
                .into(mHeaderBackground);

        Glide.with(mHeaderProfileImage)
                .load("https://www.gravatar.com/avatar/asdfasdfsd?d=retro")
                .into(mHeaderProfileImage);

        createMenuWithUserSpecificOptions(null);

        // Make sure the generic title of the fragment is restored, in case of user logout.
        if (mTopListener != null) mTopListener.updateTitle(
                getString(R.string.profile_menu_tile));
    }

    private void createMenuWithUserSpecificOptions(User user) {
        MainMenuAdapter menuAdapter = new MainMenuAdapter();

        if (user != null) {
            menuAdapter
                    .addButtonItem(getString(R.string.main_menu_item_view_profile_text),
                            DrawableUtils.applyTintToDrawable(
                                    getResources().getDrawable(R.drawable.generic_user_icon),
                                    R.color.colorPrimary),
                                    new ProfileButtonHandler())
                    .addButtonItem(getString(R.string.main_menu_item_conference_info_text),
                            DrawableUtils.applyTintToDrawable(
                                    getResources().getDrawable(R.drawable.ecescon_icon),
                                    R.color.colorPrimary),
                                    new InfoButtonHandler())
                    .addButtonItem(getString(R.string.main_menu_item_change_account_text),
                            DrawableUtils.applyTintToDrawable(
                                    getResources().getDrawable(R.drawable.logout_icon),
                                    R.color.colorPrimary),
                                    new LogoutButtonHandler())
                    .addButtonItem(getString(R.string.main_menu_item_settings_text),
                            DrawableUtils.applyTintToDrawable(
                                    getResources().getDrawable(R.drawable.icon_settings),
                                    R.color.colorPrimary),
                                    new SettingsButtonHandler())
                    .addButtonItem(getString(R.string.main_menu_item_about_text),
                            DrawableUtils.applyTintToDrawable(
                                    getResources().getDrawable(R.drawable.info_icon),
                                    R.color.colorPrimary),
                                    new AboutButtonHandler());

        } else {
            menuAdapter
                    .addButtonItem(getString(R.string.main_menu_item_sign_in),
                            DrawableUtils.applyTintToDrawable(
                                    getResources().getDrawable(R.drawable.icon_sign_in),
                                    R.color.colorPrimary),
                                    new LoginButtonHandler())
                    .addButtonItem(getString(R.string.main_menu_item_conference_info_text),
                            DrawableUtils.applyTintToDrawable(
                                    getResources().getDrawable(R.drawable.ecescon_icon),
                                    R.color.colorPrimary),
                                    new InfoButtonHandler())
                    .addButtonItem(getString(R.string.main_menu_item_settings_text),
                            DrawableUtils.applyTintToDrawable(
                                    getResources().getDrawable(R.drawable.icon_settings),
                                    R.color.colorPrimary),
                            new SettingsButtonHandler())
                    .addButtonItem(getString(R.string.main_menu_item_about_text),
                            DrawableUtils.applyTintToDrawable(
                                    getResources().getDrawable(R.drawable.info_mark),
                                    R.color.colorPrimary),
                                    new AboutButtonHandler());

        }

        mItemsList.setAdapter(menuAdapter);
    }


    private class LoginButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            displayLoginDialog();
        }
    }

    private class LogoutButtonHandler implements  View.OnClickListener {
        @Override
        public void onClick(View v) {
            UserManager.getUserManager().logout();
        }
    }

    private class ProfileButtonHandler implements  View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mTopListener != null) {
                mTopListener.navigateToNavigableKey(new NavigableKey() {
                        @Override
                        public String getKey() {
                            return "user_profile_fragment";
                        }

                        @Override
                        public Fragment createFragment() {
                            return UserProfileFragment.newInstance(
                                    UserManager.getUserManager().getCurrentUser());
                        }
                }, true);
            }
        }
    }

    private class InfoButtonHandler implements  View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mTopListener != null) {
                mTopListener.navigateToNavigableKey(new NavigableKey() {
                    @Override
                    public String getKey() {
                        return "conference_info_fragment";
                    }

                    @Override
                    public Fragment createFragment() {
                        return new InfoFragment();
                    }
                }, true);
            }
        }
    }

    private class AboutButtonHandler implements  View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mTopListener != null) {
                mTopListener.navigateToNavigableKey(new NavigableKey() {
                    @Override
                    public String getKey() {
                        return "about_fragment";
                    }

                    @Override
                    public Fragment createFragment() {
                        return new AboutFragment();
                    }
                }, true);
            }
        }
    }

    private class SettingsButtonHandler implements  View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mTopListener != null) {
                mTopListener.navigateToNavigableKey(new NavigableKey() {
                    @Override
                    public String getKey() {
                        return "settings_fragment";
                    }

                    @Override
                    public Fragment createFragment() {
                        return new SettingsFragment();
                    }
                }, true);
            }
        }
    }
}
