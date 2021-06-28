package com.example.petify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class DonateActivity extends AppCompatActivity {
    private EditText editTextName,editTextType,editTextAge,editTextColor,editTextBreed,editTextContact,editTextAddress;
    private RadioGroup radioGroup;
    private Button buttonUpload,buttonSubmit;
    private ProgressBar progressBar;
    public static final int PICK_IMAGE_REQUEST=22;

    private Uri filePath;
    private String imageUrl;

    // instance for firebase StorageReference
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().getReference("AnimalImages");

        radioGroup=findViewById(R.id.radio_group);
        editTextName=findViewById(R.id.animal_name);
        editTextAge=findViewById(R.id.animal_age);
        editTextBreed=findViewById(R.id.animal_breed);
        editTextColor=findViewById(R.id.animal_color);
        editTextType=findViewById(R.id.animal_type);
        editTextContact=findViewById(R.id.animal_contact);
        editTextAddress=findViewById(R.id.animal_address);
        progressBar=findViewById(R.id.progress_bar);

        buttonUpload=findViewById(R.id.animal_upload_buttton);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        buttonSubmit=findViewById(R.id.animal_submit_buttton);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAnimalInfo();
            }
        });
    }

    // Select Image method
    private void selectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            uploadImage();
        }

    }

    //to get extension of file
    private String getFileExtension(Uri uri) {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(filePath));
            fileReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(DonateActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl = uri.toString();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DonateActivity.this, "Failed to upload!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadAnimalInfo(){
        progressBar.setVisibility(View.VISIBLE);
        String name=editTextName.getText().toString().trim();
        String age=editTextAge.getText().toString().trim();
        String type=editTextType.getText().toString().trim();
        String breed=editTextBreed.getText().toString().trim();
        String color=editTextColor.getText().toString().trim();
        String contactNumber=editTextContact.getText().toString().trim();
        String address=editTextAddress.getText().toString().trim();
        String gender;

        int genderId=radioGroup.getCheckedRadioButtonId();
        if(genderId==R.id.gender_male){
            gender="male";
        }
        else{
            gender="female";
        }

        Animal animal=new Animal(name,age,color,breed,type,gender,imageUrl,contactNumber,address);

        String animalId=System.currentTimeMillis()+"";
        FirebaseDatabase.getInstance().getReference("Animals")
                .child(animalId)
                .setValue(animal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DonateActivity.this, "Your animal has been recorded for adoption", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DonateActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        FirebaseDatabase.getInstance().getReference("User-Animal/"+FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(animalId)
                .setValue(animalId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("DonateActivity", "onComplete: user animal pair added to firebase");
            }
        });


    }

}