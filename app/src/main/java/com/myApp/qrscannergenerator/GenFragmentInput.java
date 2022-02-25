package com.myApp.qrscannergenerator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.myApp.qrscannergenerator.GenFragmentInputDirections;
import com.myApp.qrscannergenerator.R;


public class GenFragmentInput extends Fragment {

    private EditText inputKey;
    private EditText inputText;
    private CheckBox encryptCheck;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gen_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputKey = view.findViewById(R.id.encryptKey);
        inputText = view.findViewById(R.id.textToGenerate);
        encryptCheck = view.findViewById(R.id.encryptCheckBox);

        view.findViewById(R.id.GenerateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(inputText.getText())){//check to see if no text was given
                    inputText.setError("Text or a URL is required to generate a QR Code");
                }
                else if(inputText.getText().toString().length()>2953){ //check to see if text exceeds maximum amount of characters
                    inputText.setError("Text exceeds maximum number of Characters");
                }
                else{
                    if(encryptCheck.isChecked()) { //if encryption is used
                        if(TextUtils.isEmpty(inputKey.getText())){//check to see if no key was given
                            inputKey.setError("A key is required for Encryption");
                        }
                        else{
                            try {
                                String header = inputText.getText().toString();
                                String encryptedText = (Cryptography.encrypt(header, inputKey.getText().toString()));
                                //set up navigation action using the current text as the argument
                                GenFragmentInputDirections.ActionGenFragmentInputToGenFragmentOutput action = GenFragmentInputDirections.actionGenFragmentInputToGenFragmentOutput(encryptedText, inputKey.getText().toString(), true);
                                //perform the above action to navigate to the decrypt fragment
                                NavHostFragment.findNavController(GenFragmentInput.this).navigate(action);

                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "An error has occurred. Please try a different key", Toast.LENGTH_SHORT);
                            }
                        }
                    }
                    else{//if incryption is not sused
                        String text = inputText.getText().toString(); //get the current text
                        //set up navigation action using the current text as the argument
                        GenFragmentInputDirections.ActionGenFragmentInputToGenFragmentOutput action = GenFragmentInputDirections.actionGenFragmentInputToGenFragmentOutput(text, "", false);
                        //perform the above action to navigate to the decrypt fragment
                        NavHostFragment.findNavController(GenFragmentInput.this).navigate(action);
                    }
                }
            }
        });

        encryptCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(encryptCheck.isChecked()){
                    inputKey.setVisibility(View.VISIBLE);
                }
                else{
                    inputKey.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}