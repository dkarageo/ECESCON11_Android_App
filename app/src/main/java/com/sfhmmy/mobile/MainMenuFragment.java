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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TopLevelFragmentEventsListener mTopListener;

    public MainMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainMenuFragment newInstance(String param1, String param2) {
        MainMenuFragment fragment = new MainMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
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
}
