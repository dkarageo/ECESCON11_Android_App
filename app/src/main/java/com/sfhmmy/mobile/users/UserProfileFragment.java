/*
 * UserProfileFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.users;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.TopLevelFragmentEventsListener;
import com.sfhmmy.mobile.utils.DrawableUtils;
import com.sfhmmy.mobile.utils.TextUtils;


public class UserProfileFragment extends Fragment {

    private static final String USER_OBJECT_KEY = "user_object";

    private TopLevelFragmentEventsListener mTopListener;

    // Layout views handlers.
    private CircleImageView          mProfileImage;
    private MaterialEditText         mUserName;
    private MaterialEditText         mUserSurname;
    private ImageButton              mEditNameButton;
    private ExpandableHeightListView mUserDetailList;
    private TextView                 mUserEmail;

    private User mAttachedUser;  // User to display;


    public static UserProfileFragment newInstance(User user) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(USER_OBJECT_KEY, user);
        fragment.setArguments(bundle);
        return fragment;
    }

    public UserProfileFragment() {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mAttachedUser = savedInstanceState.getParcelable(USER_OBJECT_KEY);
        } else if (getArguments() != null) {
            mAttachedUser = getArguments().getParcelable(USER_OBJECT_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_user_profile, container, false);

        mProfileImage   = root.findViewById(R.id.user_profile_user_photo);
        mUserName       = root.findViewById(R.id.user_profile_user_name);
        mUserSurname    = root.findViewById(R.id.user_profile_user_surname);
        mEditNameButton = root.findViewById(R.id.user_profile_edit_fullname_button);
        mUserDetailList = root.findViewById(R.id.user_profile_user_detail_list);
        mUserEmail      = root.findViewById(R.id.user_profile_email);

        mEditNameButton.setOnClickListener(new View.OnClickListener() {

            private boolean isEditModeOn = false;

            @Override
            public void onClick(View v) {
                if (isEditModeOn) {
                    mEditNameButton.setBackground(DrawableUtils.applyTintToDrawable(
                            App.getAppResources().getDrawable(R.drawable.icon_edit),
                            R.color.colorPrimaryDark
                    ));

                    mUserName.setEnabled(false);
                    mUserSurname.setEnabled(false);
                    mUserName.setHideUnderline(true);
                    mUserSurname.setHideUnderline(true);

                    // TODO: Send request.

                } else {
                    mEditNameButton.setBackground(DrawableUtils.applyTintToDrawable(
                            App.getAppResources().getDrawable(R.drawable.check_mark),
                            R.color.colorPrimaryDark
                    ));

                    mUserName.setEnabled(true);
                    mUserSurname.setEnabled(true);
                    mUserName.setHideUnderline(false);
                    mUserSurname.setHideUnderline(false);

                    mUserName.requestFocus();
                    InputMethodManager imm = (InputMethodManager)
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mUserName, InputMethodManager.SHOW_IMPLICIT);
                }

                isEditModeOn = !isEditModeOn;
            }
        });
        mEditNameButton.setBackground(DrawableUtils.applyTintToDrawable(
                App.getAppResources().getDrawable(R.drawable.icon_edit),
                R.color.colorPrimaryDark
        ));

        // TODO: Allow edit of name.
        mEditNameButton.setVisibility(View.GONE);

        if (mAttachedUser != null) bindUser(mAttachedUser);
        mUserName.setEnabled(false);
        mUserSurname.setEnabled(false);
        mUserName.setHideUnderline(true);
        mUserSurname.setHideUnderline(true);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mTopListener != null) {
            mTopListener.updateTitle(getString(R.string.user_profile_action_bar_title));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTopListener = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(USER_OBJECT_KEY, mAttachedUser);
    }

    private void bindUser(User user) {
        mUserName.setText(user.getName());
        mUserSurname.setText(user.getSurname());
        mUserEmail.setText(user.getEmail());

        Glide.with(mProfileImage)
             .load(user.getProfilePictureURL())
             .into(mProfileImage);

        String absenceString = getString(R.string.user_profile_field_absence_text);

        // TODO: Allow editing of fields.
        EditableListAdapter adapter = new EditableListAdapter();
        adapter.addEditableItem(user.getGenderText() != null ? user.getGenderText() : absenceString,
                                getString(R.string.user_profile_gender_field_title),
                                null,
                                null,
                                false)
               .addEditableItem(user.getPreferedLanguage() != null ?
                                        user.getPreferedLanguage() : absenceString,
                                getString(R.string.user_profile_certificate_language_field_title),
                                null,
                                null,
                                false)
               .addEditableItem(user.getEducationLevel() != null ?
                                        user.getEducationLevel() : absenceString,
                                getString(R.string.user_profile_education_level_field_title),
                                null,
                                null,
                                false)
               .addEditableItem(user.getOrganization() != null ?
                                        user.getOrganization() : absenceString,
                                getString(R.string.user_profile_organization_field_title),
                                null,
                                null,
                                false)
               .addEditableItem(user.getDepartment() != null ? user.getDepartment() : absenceString,
                                getString(R.string.user_profile_department_field_title),
                                null,
                                null,
                                false)
               .addEditableItem(user.getDepartmentSpecialization() != null ?
                                        user.getDepartmentSpecialization() : absenceString,
                                getString(R.string.user_profile_department_specialization_field_title),
                                null,
                                null,
                                false)
               .addEditableItem(user.getYearsOfExperience() > 0 ?
                                        TextUtils.integerToOrdinalString(user.getYearsOfExperience()) :
                                        absenceString,
                                getString(R.string.user_profile_years_of_experience_field_title),
                                null,
                                null,
                                false);
        mUserDetailList.setAdapter(adapter);
        mUserDetailList.setExpanded(true);
        mUserDetailList.setVerticalScrollBarEnabled(false);
    }
}
