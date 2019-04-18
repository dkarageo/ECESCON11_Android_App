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
import android.graphics.BitmapFactory;
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
import com.sfhmmy.mobile.cache.CacheProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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

    private static final String QR_IMAGE_CACHE_KEY_BASE = "PassportFragment.QR_IMAGE_";

    private TopLevelFragmentEventsListener mTopListener;

    private ImageView          mQRCode;
    private CircleImageView    mProfilePicture;
    private TextView           mNameSurname;
    private TextView           mUserEmail;
    private TextView           mUserOrg;
    private TextView           mUserRole;
    private View               mUserCardContainer;
    private View               mUserMissingCardContainer;
    private Button             mLoginButton;
    private SwipeRefreshLayout mRefresher;

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
        mRefresher                = root.findViewById(R.id.passport_refresher);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLoginDialog();
            }
        });

        // Make sure that profile picture is not hidden by qr code wrapper.
        mProfilePicture.bringToFront();

        // Setup user refresher.
        mRefresher.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        mRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UserManager manager = UserManager.getUserManager();
                // Make sure that spinning icon is displayed only when user manager is going to
                // start a new refresh process, so it will be cleared on refresh.
                if (!manager.isUserRefreshing()) UserManager.getUserManager().refreshUser();
            }
        });

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

        mRefresher.setRefreshing(false);
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
        if (qrImg == null && user.getPassportValue() != null) {
            CacheProvider cache = CacheProvider.getCacheProvider();
            SerialBitmap serialBitmap = (SerialBitmap) cache.retrieveObject(String.format(
                    "%s%s", QR_IMAGE_CACHE_KEY_BASE+user.getPassportValue(), user.getPassportValue()
            ));

            if (serialBitmap == null) {
                qrImg = generateQRCodeBitmap(String.format("ecescon11://%s", user.getPassportValue()));

                serialBitmap = new SerialBitmap(qrImg);
                cache.storeObject(String.format(
                        "%s%s", QR_IMAGE_CACHE_KEY_BASE+user.getPassportValue(), user.getPassportValue()),
                        serialBitmap
                );

            } else {
                qrImg = serialBitmap.getBitmap();
            }
        }

        if (qrImg != null) mQRCode.setImageBitmap(qrImg);
        if (name  != null) mNameSurname.setText(String.format("%s %s", name, surname));
        if (email != null) mUserEmail.setText(email);
        if (org   != null) mUserOrg.setText(org);

        updateUserRole(user);

        Glide.with(mProfilePicture)
                .load(user.getProfilePictureURL())
                .dontAnimate()
                .into(mProfilePicture);

        mRefresher.setEnabled(true);
    }

    private void displayUserMissingCard() {
        mUserCardContainer.setVisibility(View.GONE);
        mUserMissingCardContainer.setVisibility(View.VISIBLE);

        mRefresher.setEnabled(false);
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


    private class SerialBitmap implements Serializable {

        private Bitmap mBitmap;

        SerialBitmap(Bitmap bitmap) {
            mBitmap = bitmap;
        }

        public Bitmap getBitmap() { return mBitmap; }

        // Converts the Bitmap into a byte array for serialization
        private void writeObject(java.io.ObjectOutputStream out) throws IOException {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
            byte bitmapBytes[] = byteStream.toByteArray();
            out.write(bitmapBytes, 0, bitmapBytes.length);
        }

        // Deserializes a byte array representing the Bitmap and decodes it.
        private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int b;
            while((b = in.read()) != -1)
                byteStream.write(b);
            byte bitmapBytes[] = byteStream.toByteArray();
            mBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        }
    }
}