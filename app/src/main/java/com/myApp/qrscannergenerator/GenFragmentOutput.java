package com.myApp.qrscannergenerator;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myApp.qrscannergenerator.GenFragmentOutputArgs;
import com.myApp.qrscannergenerator.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.vmadalin.easypermissions.EasyPermissions;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;


public class GenFragmentOutput extends Fragment {

    private ImageView qrCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gen_output, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        //Setup
        GenFragmentOutputArgs args = GenFragmentOutputArgs.fromBundle(getArguments());
        String textToGen = args.getTextToGen(); //retrieve the text used to generate a qr code
        qrCode = view.findViewById(R.id.qrImage);
        TextView keyTextview = view.findViewById(R.id.isEncryptedTextView);

        //set visibility of encrypt text depending on if it was encrypted
        if(args.getIsEncrypted()){
            keyTextview.setVisibility(View.VISIBLE);
            keyTextview.setText("Encrypted using the key: " + args.getKey()); // change text to include text
        }


        //Generating qr code
        //initialize the multi format writer
        MultiFormatWriter write = new MultiFormatWriter();
        // Initialize the bit matrix
        try {
            BitMatrix matrix = write.encode(textToGen, BarcodeFormat.QR_CODE, 800, 800);
            //Initialize Barcode encoder
            BarcodeEncoder encoder = new BarcodeEncoder();
            //initialize bitmap using the bit matrix
            Bitmap bitmap = encoder.createBitmap(matrix);
            //Set the image view to use this bitmap
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e){
            e.printStackTrace();
        }
    //Saving image to gallery functionality

        view.findViewById(R.id.downloadButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //asking user for storage permission
                requestStoragePermission("");
                if(hasStoragePermission()) {

                    //getting the imageview's bitmap
                    Bitmap bitmap = ((BitmapDrawable) qrCode.getDrawable()).getBitmap();

                    //set up the directory
                    File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File dir = new File(file.getAbsolutePath() + "/Camera");
                    dir.mkdirs();
                    //filename uses the current time to avoid accidentally creating a file with the same name as another
                    String filename = String.format("%d.png", System.currentTimeMillis());
                    File outFile = new File(dir, filename);
                    try {
                        FileOutputStream outputStream = new FileOutputStream(outFile);

                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    MediaScannerConnection.scanFile(getActivity(), new String[]{outFile.getPath()}, new String[]{"image/png"}, null);
                }
                else{
                    requestStoragePermission("In order to scan, the permission to use access storage is required. This permission can always be changed in your devices settings");
                }
            }
        });



    }
    private void requestStoragePermission(String rationale) {
        EasyPermissions.requestPermissions(this,
                rationale, //rational given to user upon request of camera
                1,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    private boolean hasStoragePermission(){ //simple check to see if the camera permission was given to this activity
        return EasyPermissions.hasPermissions(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
}