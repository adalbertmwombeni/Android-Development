package com.example.ksbsmartdevice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.login.LoginException;


//import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
//import com.amazonaws.services.s3.AmazonS3Client;

//package com.example.ksbsmartdevice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.login.LoginException;


import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.util.IOUtils;


public class MainActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    ImageView selectedImage;
    Button cameraBtn, galleryBtn;
    String currentPhotoPath;
    private final String KEY = "A";
    private final String SECRET = "B";
    private BasicAWSCredentials credentials;
    private AmazonS3Client s3Client;
    private Uri fileUri;
    String fileName;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));
                Toast.makeText(MainActivity.this, "====The path is also available==", Toast.LENGTH_SHORT).show();

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                fileUri = contentUri;
                fileName = f.getName();
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                Toast.makeText(MainActivity.this, "====>>>> The content is sent", Toast.LENGTH_SHORT).show();
                credentials = new BasicAWSCredentials(KEY, SECRET);
                s3Client = new AmazonS3Client(credentials);
                uploadToAWS();
                Toast.makeText(MainActivity.this, "==DONE==", Toast.LENGTH_SHORT).show();

            }

        }
        //if (requestCode == CAMERA_REQUEST_CODE) {
        //if (resultCode == Activity.RESULT_OK){
        //File f = new File(currentPhotoPath);
        //selectedImage.setImageURI(Uri.fromFile(f));

        //Log.d("tag", "Absolute Url of image is " +Uri.fromFile(f));
        //}

        //}
    }
    // final Uri contentUri
    private void uploadToAWS() {

        ////
        Toast.makeText(MainActivity.this, "====NIRAKANANINYIRAKANANI==", Toast.LENGTH_SHORT).show();
        if (fileUri != null) {

            //final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                   // "/");


            //createFile(getApplicationContext(), fileUri, new File(fileName));

            ////
            Log.d("tag", "the url is  " + fileUri);
            Log.d("tag", "the file name is  " + currentPhotoPath);
            Log.d("tag", "the INPUT file is  " + new File(currentPhotoPath));
            TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(getApplicationContext())
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())   //"name", new File("Uri")
                            .s3Client(s3Client)
                            .build();

            TransferObserver uploadobserver =
                    transferUtility.upload("orsstorage1","fileUri", new File(currentPhotoPath));

            uploadobserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        Toast.makeText(MainActivity.this, "Upload completed", Toast.LENGTH_SHORT).show();
                    } else if (TransferState.FAILED == state) {
                        Toast.makeText(MainActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {

                }
            });

        }
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    private void createFile(Context applicationContext, Uri fileUri, File file) {
        try {
            Context context = null;
            Uri srcUri = null;
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            String dstFile = null;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        System.out.println("image");
        return image;
    }

    /*
    private void createImageFile() throws IOException {
        //create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "png_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".png", storageDir);

        //save a file: path for use with ACTION_VIEW intents
        ////currentPhotoPath = image.getAbsolutePath();
        ////File f = new File(currentPhotoPath);
        ////selectedImage.setImageURI(Uri.fromFile(f));
        ///Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));


    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedImage = findViewById(R.id.displayImageView);
        cameraBtn = findViewById(R.id.cameraBtn);
        galleryBtn = findViewById(R.id.galleryBtn);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Camera is clicked", Toast.LENGTH_SHORT).show();
                //openCamera();
                dispatchTakePictureIntent();

            }
        });


    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Toast.makeText(MainActivity.this, "....File is created....", Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(MainActivity.this, "....Exception is thrown....", Toast.LENGTH_SHORT).show();

            }
            //Toast.makeText(MainActivity.this, "....please wait....", Toast.LENGTH_SHORT).show();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Toast.makeText(MainActivity.this, "...I am alread in....", Toast.LENGTH_SHORT).show();
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }


    }
}















