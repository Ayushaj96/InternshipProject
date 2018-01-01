package com.example.vasu.aismap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profilePhoto ;
    TextView tvName,tvUsername,tvMobile,tvEmail,tvDob,tvProfession ;
   // Integer REQUEST_CAMERA=1,SELECT_FILE=0;
    SharedPreferences sharedPreferences,sharedPreferencesPhoto ;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences=getApplicationContext().getSharedPreferences("MyInfo", MODE_PRIVATE);
        sharedPreferencesPhoto=getApplicationContext().getSharedPreferences("ProfilePhoto",MODE_PRIVATE);
        editor = sharedPreferencesPhoto.edit();


        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvMobile = findViewById(R.id.tvMobile);
        tvEmail = findViewById(R.id.tvEmail);
        tvDob = findViewById(R.id.tvDob);
        tvProfession = findViewById(R.id.tvProfession);

        profilePhoto = findViewById(R.id.profile_image);

        tvName.setText(sharedPreferences.getString("FullName" , ""));
        tvUsername.setText(sharedPreferences.getString("Username" , ""));
        tvMobile.setText(sharedPreferences.getString("Mobile" , ""));
        tvEmail.setText(sharedPreferences.getString("Email" , ""));
        tvDob.setText(sharedPreferences.getString("DOB" , ""));
        tvProfession.setText(sharedPreferences.getString("Profession" , ""));

        if(!sharedPreferencesPhoto.getString("Path","").equals(""))
        { Uri myUri = Uri.parse(sharedPreferencesPhoto.getString("Path",""));
            profilePhoto.setImageURI(myUri);
        }

      profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileActivity.this);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                profilePhoto.setImageURI(resultUri);
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    saveToInternalStorage(bitmap);}
                catch (Exception e)
                {

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {//ERROR

                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveToInternalStorage(Bitmap finalBitmap){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            editor.putString("Path", String.valueOf(Uri.fromFile(file)));
            editor.commit();
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
