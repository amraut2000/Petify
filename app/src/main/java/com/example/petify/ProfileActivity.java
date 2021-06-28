package com.example.petify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    private Button buttonSignOut,buttonAdopt,buttonDonate,buttonYourPets;
    private TextView textViewEmail,textViewName;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        buttonSignOut=findViewById(R.id.signOutButton);
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
            }
        });

        buttonDonate=findViewById(R.id.donate);
        buttonDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,DonateActivity.class));
            }
        });

        buttonAdopt=findViewById(R.id.adopt);
        buttonAdopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,AllPetsActivity.class));
            }
        });

        buttonYourPets=findViewById(R.id.your_pets);
        buttonYourPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,UserPet.class));
            }
        });

        textViewEmail=findViewById(R.id.email);
        textViewName=findViewById(R.id.name);
        imageView=findViewById(R.id.profilephoto);


        user=FirebaseAuth.getInstance().getCurrentUser();
        userID=user.getUid();
        reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Log.d("Image Url: ", "onDataChange: "+user.profileUrl);
                Picasso.get().load(user.profileUrl).into(imageView);
                textViewEmail.setText(user.email);
                textViewName.setText(user.fullname);
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });


    }
}