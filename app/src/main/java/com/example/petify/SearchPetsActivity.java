package com.example.petify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.petify.Adapters.AllPetsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchPetsActivity extends AppCompatActivity {
    private List<Animal> animals;
    private RecyclerView recyclerView;
    private AllPetsAdapter allPetsAdapter;

    private ProgressBar progressBar;

    private EditText editTextSearch;
    private Button buttonSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_search_pets);

        animals = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView_pets1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        allPetsAdapter = new AllPetsAdapter(animals, this);
        recyclerView.setAdapter(allPetsAdapter);

        progressBar = findViewById(R.id.progress_bar);
        editTextSearch = findViewById(R.id.search_editText);
        buttonSearch = findViewById(R.id.search_button);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = editTextSearch.getText().toString().trim().toLowerCase();
                if (type.isEmpty()) {
                    editTextSearch.setError("Type is required");
                    editTextSearch.requestFocus();
                    return;
                }
                searchPet(type);
            }
        });
    }

    private void searchPet(String type) {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = mFirebaseDatabaseReference.child("Animals").orderByChild("type").equalTo(type);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                animals.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Animal animal = dataSnapshot.getValue(Animal.class);
                    animals.add(animal);
                }
                if (animals.size() == 0) {
                    Toast.makeText(SearchPetsActivity.this, "No pet available of this type", Toast.LENGTH_LONG).show();
                }
                allPetsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SearchPetsActivity.this, "Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}