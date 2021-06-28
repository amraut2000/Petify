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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllPetsActivity extends AppCompatActivity {
    private List<Animal> animals;
    private RecyclerView recyclerView;
    private AllPetsAdapter allPetsAdapter;

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_all_pets);

        animals = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView_pets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        databaseReference= FirebaseDatabase.getInstance().getReference("Animals");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                animals.clear();
                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    Animal animal=postSnapshot.getValue(Animal.class);
                    Log.d("AllPetsActivity", "onDataChange: "+animal.getName());
                    animals.add(animal);
                }
                allPetsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllPetsActivity.this, "Failed to read data from database!", Toast.LENGTH_SHORT).show();
            }
        });

        allPetsAdapter = new AllPetsAdapter(animals, this);
        recyclerView.setAdapter(allPetsAdapter);

    }
}