package com.myApp.qrscannergenerator;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.myApp.qrscannergenerator.R;
import com.myApp.qrscannergenerator.ScanFragmentInputDirections;
import com.google.zxing.Result;
import com.vmadalin.easypermissions.EasyPermissions;


public class ScanFragmentInput extends Fragment {

    private CodeScanner mCodeScanner;
    private final int Cam_RequestCode =101;
    TextView showDecodedTextView;
    private boolean firstDecode = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scan_input, container, false);

        Activity currentAct = requireActivity();
        //set up textview so that it can be edited when it had been decoded
        showDecodedTextView = root.findViewById(R.id.scanner_TextView);

        //Handling first time permissions
        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Toast.makeText(currentAct, "Permission Granted", Toast.LENGTH_SHORT).show();
                        // Permission is granted. Continue the workflow
                        codeScanner(root); //call codeScanner function to set up mCodeScanner
                    } else {
                        Log.e("onCreateView: ", "Permission Not granted");
                        Toast.makeText(currentAct, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                        requestCameraPermission();
                    }
                });


        if (hasCameraPermission()) {
            codeScanner(root); //call codeScanner function to set up mCodeScanner
        } else {
            // Permission has not been asked
            // The registered ActivityResultCallback gets the result of this request.
            Log.e("onCreateView: ", "Request perm");
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }




        return root;
    }

    private void codeScanner(View root) {
        Activity currentAct = requireActivity();
        //instantiate activity to be used in codescanner constructor and decodecallback

        mCodeScanner = new CodeScanner(currentAct, root.findViewById(R.id.scanner_viewfinder)); //instantiate mCodeScanner

        //apply the correct parameters
        mCodeScanner.setCamera(CodeScanner.CAMERA_BACK); //Set camera to use the rear camera
        mCodeScanner.setFormats(CodeScanner.ALL_FORMATS); //Set to use all the supported formats
        mCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE); //Auto focuses at fixed intervals to ensure compatibility with older devices
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);//The scanner will continuously scan rather than stopping after 1 successful scan
        mCodeScanner.setAutoFocusEnabled(true);//Enables auto focus by default (can be turned off)
        mCodeScanner.setFlashEnabled(false);//Flash will be disabled by default. flash will not come on when opening scanner

        //Callback happens on a successful scan and updates the text
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull Result result) {

                    if(!result.getText().contentEquals(showDecodedTextView.getText())) { //if a new code is scanned then update the textview
                        currentAct.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDecodedTextView.setText(result.getText()); //changes scanner_textview to the result of the scan
                                //autolink is set to web so that links become hyperlinks.
                                if(firstDecode) { //if this is the first time a code has been decoded since the fragment was created, enable the encrypt button
                                    View encryptButton = root.findViewById(R.id.decryptButton);
                                    encryptButton.setAlpha(1f);
                                    encryptButton.setEnabled(true);
                                    firstDecode = false;
                                    Log.e("onDecoded:", "first");
                                }
                            }

                        });
                    }
                }
        });
        mCodeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Exception error) {
                currentAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("ScanFrag", "Camera initialization error: " + error.getMessage()); //send error log message
                    }
                });
            }
        });

    }

    @Override
    public void onResume() { //when app is left and then reopened
        super.onResume();
        if(!(mCodeScanner == null)) {
            mCodeScanner.startPreview();} //start scanning once again when app is reopened
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!(mCodeScanner == null)) mCodeScanner.releaseResources(); //prevents memory leaks and frees up the resources while app is not in use
    }

    private boolean hasCameraPermission(){ //simple check to see if the camera permission was given to this activity
        return EasyPermissions.hasPermissions(requireActivity(), Manifest.permission.CAMERA);
    }
    private void requestCameraPermission() {
        EasyPermissions.requestPermissions(this,
                "In order to scan, the permission to use the camera is required.", //rational given to user upon request of camera
                Cam_RequestCode,
                Manifest.permission.CAMERA);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.decryptButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Sending the current encrypted text to the next fragment to be decrypted.
                String currentDecodedText = showDecodedTextView.getText().toString(); //get the current text
                //set up navigation action using the current text as the argument
                ScanFragmentInputDirections.ActionScanFragmentInputToScanFragmentOutput action = ScanFragmentInputDirections.actionScanFragmentInputToScanFragmentOutput(currentDecodedText);
                //perform the above action to navigate to the decrypt fragment
                NavHostFragment.findNavController(ScanFragmentInput.this).navigate(action);
            }


        });
    }
}



