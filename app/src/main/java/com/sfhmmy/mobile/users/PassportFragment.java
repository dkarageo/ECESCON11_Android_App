/**
 * PassportFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */
package com.sfhmmy.mobile.users;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.UserAwareFragment;
import com.sfhmmy.mobile.TopLevelFragmentEventsListener;

import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * PassportFragment provides a view of user's conference card.
 *
 * It displays a QRCode suitable for check-in system along with basic user information.
 *
 * It can be used as the main content of an activity, by setting that activity as a
 * TopLevelFragmentEventsListener.
 */
public class PassportFragment extends UserAwareFragment {

    private TopLevelFragmentEventsListener mTopListener;

    private ImageView       mQRCode;
    private CircleImageView mProfilePicture;
    private TextView        mNameSurname;
    private TextView        mUserEmail;
    private TextView        mUserOrg;
    private TextView        mUserRole;
    private View            mUserCardContainer;
    private View            mUserMissingCardContainer;
    private Button          mLoginButton;

    private Bitmap qrImg = null;  // Bitmap of QR Code, generated once upon fragment's creation.


    public PassportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TopLevelFragmentEventsListener) {
            mTopListener = (TopLevelFragmentEventsListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                                       " must implement TopLevelFragmentEventsListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_passport, container, false);

        // Acquire handlers to UI elements.
        mQRCode                   = root.findViewById(R.id.passport_qrcode);
        mProfilePicture           = root.findViewById(R.id.passport_profile_img);
        mNameSurname              = root.findViewById(R.id.passport_name_surname);
        mUserEmail                = root.findViewById(R.id.passport_email);
        mUserOrg                  = root.findViewById(R.id.passport_organization);
        mUserRole                 = root.findViewById(R.id.passport_role);
        mUserCardContainer        = root.findViewById(R.id.passport_user_card_container);
        mUserMissingCardContainer = root.findViewById(R.id.passport_user_missing_card_container);
        mLoginButton              = root.findViewById(R.id.passport_unlogged_user_notification_login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLoginDialog();
            }
        });

        // Make sure that profile picture is not hidden by qr code wrapper.
        mProfilePicture.bringToFront();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mTopListener != null) mTopListener.updateTitle(getString(R.string.passport_title));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTopListener = null;
    }

    @Override
    protected void onCreateUserSpecificContent(User user) {
        super.onCreateUserSpecificContent(user);

        if (user != null) displayUserCard(user);
        else displayUserMissingCard();
    }

    private void displayUserCard(User user) {
        // Display the user's card.
        mUserCardContainer.setVisibility(View.VISIBLE);
        mUserMissingCardContainer.setVisibility(View.GONE);

        // Fill in user's detail.
        String email   = user.getEmail();
        String name    = user.getName();
        String surname = user.getSurname();
        String org     = user.getOrganization();

        // The first time fragment is created, generate a QRCode Bitmap.
        // TODO: Decouple QR Bitmap generation from fragment's lifecycle.
        if (qrImg == null) qrImg = email != null ? generateQRCodeBitmap(email) : null;

        if (qrImg != null) mQRCode.setImageBitmap(qrImg);
        if (name  != null) mNameSurname.setText(String.format("%s %s", name, surname));
        if (email != null) mUserEmail.setText(email);
        if (org   != null) mUserOrg.setText(org);

        updateUserRole(user);

        Glide.with(mProfilePicture)
                .load(user.getProfilePictureURL())
                .dontAnimate()
                .into(mProfilePicture);
    }

    private void displayUserMissingCard() {
        mUserCardContainer.setVisibility(View.GONE);
        mUserMissingCardContainer.setVisibility(View.VISIBLE);
    }

    private void updateUserRole(User user) {

        switch (user.getRole()) {
            case ADMINISTRATOR:
                mUserRole.setText(getString(R.string.passport_user_role_administrator_text));
                mUserRole.setVisibility(View.VISIBLE);
                break;

            case SECRETARY:
                mUserRole.setText(getString(R.string.passport_user_role_secretary_text));
                mUserRole.setVisibility(View.VISIBLE);
                break;

            case VISITOR:
                mUserRole.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Generates a QRCode Bitmap encoding the provided content.
     */
    private Bitmap generateQRCodeBitmap(String content) {
        Bitmap qrCodeBmp = null;

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            qrCodeBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrCodeBmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {}

        return qrCodeBmp;
    }
}