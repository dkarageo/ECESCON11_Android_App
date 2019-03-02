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
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.TopLevelFragmentEventsListener;


/**
 * PassportFragment provides a view of user's conference card.
 *
 * It displays a QRCode suitable for check-in system along with basic user information.
 *
 * It can be used as the main content of an activity, by setting that activity as a
 * TopLevelFragmentEventsListener.
 */
public class PassportFragment extends Fragment {

    private TopLevelFragmentEventsListener mTopListener;

    private ImageView mQRCode;
    private ImageView mProfilePicture;
    private TextView  mUserName;
    private TextView  mUserEmail;
    private TextView  mUserOrg;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_passport, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Acquire handlers to UI elements.
        mQRCode         = (ImageView) getActivity().findViewById(R.id.passport_qrcode);
        mProfilePicture = (ImageView) getActivity().findViewById(R.id.passport_profile_img);
        mUserName       = (TextView)  getActivity().findViewById(R.id.passport_name);
        mUserEmail      = (TextView)  getActivity().findViewById(R.id.passport_email);
        mUserOrg        = (TextView)  getActivity().findViewById(R.id.passport_organization);

        // Fill UI elements with content.
        User curUser = UserManager.getUserManager().getCurrentUser();
        if (curUser != null) {
            String email = curUser.getEmail();
            String name  = curUser.getName();
            String org   = curUser.getOrganization();
            Bitmap prImg = curUser.getProfilePicture();

            // The first time fragment is created, generate a QRCode Bitmap.
            // TODO: Decouple QR Bitmap generation from fragment's lifecycle.
            if (qrImg == null) qrImg = email != null ? generateQRCodeBitmap(email) : null;

            if (prImg != null) mProfilePicture.setImageBitmap(prImg);
            if (qrImg != null) mQRCode.setImageBitmap(qrImg);
            if (name  != null) mUserName.setText(name);
            if (email != null) mUserEmail.setText(curUser.getEmail());
            if (org   != null) mUserOrg.setText(org);
        }

        // Make sure that profile picture is not hidden by qr code wrapper.
        mProfilePicture.bringToFront();
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