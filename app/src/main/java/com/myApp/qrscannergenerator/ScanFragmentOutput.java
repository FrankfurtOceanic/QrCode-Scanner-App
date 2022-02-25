package com.myApp.qrscannergenerator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.myApp.qrscannergenerator.R;
import com.myApp.qrscannergenerator.ScanFragmentOutputArgs;


public class ScanFragmentOutput extends Fragment {

    private EditText inputKey;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_output, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String encryptedText = ScanFragmentOutputArgs.fromBundle(getArguments()).getEncryptedText(); //Retrieve the string passed in ScanFragmentInput
        TextView header = view.findViewById(R.id.textview_header);
        header.setText(encryptedText);

        inputKey = view.findViewById(R.id.inputKey); //instantiating the EditText so that its contents can be accessed in the click listener

        view.findViewById(R.id.insertKeyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(inputKey.getText())){//check to see if no key was given
                    inputKey.setError("A key is required for Decryption");
                }
                else{
                    try {
                        header.setText(Cryptography.decrypt(encryptedText, inputKey.getText().toString()));
                    }
                    catch (Exception e){
                        Toast.makeText(getActivity(), "An error has occurred. Please try a different key", Toast.LENGTH_SHORT);
                    }

                }
            }


        });


    }

}