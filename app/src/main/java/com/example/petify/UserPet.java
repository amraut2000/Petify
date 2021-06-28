package com.example.petify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.petify.Adapters.AllPetsAdapter;
import com.example.petify.Adapters.UserPetAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPet extends AppCompatActivity {
    private List<Animal> animals;
    private RecyclerView recyclerView;
    private com.example.petify.Adapters.UserPetAdapter userPetAdapter;

    private Map<String,Animal> map;

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_user_pet);

        animals = new ArrayList<>();
        map=new HashMap<>();

        recyclerView = findViewById(R.id.recyclerView_pets1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> animalId=new ArrayList<>();

        databaseReference= FirebaseDatabase.getInstance().getReference("User-Animal/"+userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                animalId.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String id=dataSnapshot.getValue(String.class);
                    //Log.d("UserPet", "onDataChange: "+id);
                    animalId.add(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserPet.this, "Failed to read data from database!", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference= FirebaseDatabase.getInstance().getReference("Animals");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                animals.clear();
                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    String id=postSnapshot.getKey();
                    Log.d("UserPet", "onDataChange: "+id);
                    for(String s:animalId){
                        if(id.equals(s)){
                            Animal animal=postSnapshot.getValue(Animal.class);
                            animals.add(animal);
                            map.put(id,animal);
                        }
                    }
                }
                userPetAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserPet.this, "Failed to read data from database!", Toast.LENGTH_SHORT).show();
            }
        });

        userPetAdapter = new UserPetAdapter(animals, this,map);
        recyclerView.setAdapter(userPetAdapter);
    }
}