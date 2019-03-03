/*
 * CameraScannerFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.checkins;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.sfhmmy.mobile.R;

import java.util.ArrayList;
import java.util.List;


public class CameraScannerFragment extends Fragment {

    private Button          mPermGrantButton;
    private TextView        mPermMissingWarning;
    private CodeScannerView mCodeScannerView;

    private CodeScanner mCodeScanner;

    private List<OnCodeFoundEventListener> mCodeFoundListeners;

    private AppCompatActivity mAttachedActivity = null;  // Current activity.

    // Indicates whether permission for accessing camera has been granted.
    private boolean mCanAccessCamera;


    public CameraScannerFragment() {
        mCodeFoundListeners = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            mAttachedActivity = (AppCompatActivity) context;
        } else {
            throw new RuntimeException(
                    "Camera scanner fragment should be attach to an AppCompatActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCanAccessCamera = ContextCompat.checkSelfPermission(
                mAttachedActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        if (!mCanAccessCamera) requestCameraPermission();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(
                R.layout.fragment_camera_scanner, container, false);

        mPermGrantButton    = rootView.findViewById(R.id.camera_scanner_permission_grant_button);
        mPermMissingWarning = rootView.findViewById(R.id.camera_scanner_permission_missing_warning);
        mCodeScannerView    = rootView.findViewById(R.id.camera_scanner);

        // Setup grant permission button.
        mPermGrantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
            }
        });

        // Setup camera scanner.
        mCodeScanner = new CodeScanner(mAttachedActivity, mCodeScannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                mAttachedActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyOnCodeFoundEventListeners(result.getText());
                    }
                });
            }
        });
        mCodeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCodeScanner.startPreview();
            }
        });
        displayCameraScanner();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCodeScanner.stopPreview();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for  (int i = 0; i < permissions.length; ++i) {
            if (permissions[i].equals(Manifest.permission.CAMERA) &&
                    grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                mCanAccessCamera = true;
                displayCameraScanner();
            }
        }
    }

    public void registerOnCodeFoundEventListener(OnCodeFoundEventListener l) {
        mCodeFoundListeners.add(l);
    }

    public void removeOnCodeFoundEventListener(OnCodeFoundEventListener l) {
        mCodeFoundListeners.remove(l);
    }

    public void notifyOnCodeFoundEventListeners(String code) {
        for (OnCodeFoundEventListener l : mCodeFoundListeners) l.onCodeFound(code);
    }

    private void requestCameraPermission() {
        requestPermissions(new String[] {Manifest.permission.CAMERA}, 0);
    }

    private void displayCameraScanner() {
        if (mCanAccessCamera) {
            mPermGrantButton.setVisibility(View.GONE);
            mPermMissingWarning.setVisibility(View.GONE);
            mCodeScannerView.setVisibility(View.VISIBLE);
        } else {
            mPermGrantButton.setVisibility(View.VISIBLE);
            mPermMissingWarning.setVisibility(View.VISIBLE);
            mCodeScannerView.setVisibility(View.GONE);
        }
    }

    public interface OnCodeFoundEventListener {
        void onCodeFound(String value);
    }
}
