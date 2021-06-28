package com.example.petify.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petify.Animal;
import com.example.petify.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class UserPetAdapter extends RecyclerView.Adapter<UserPetAdapter.ViewHolder> {
    private UserPetAdapter.OnItemClickListener listener;

    private List<Animal> dataSet;
    private Map<String,Animal> map;
    private Context context;

    public UserPetAdapter(List<Animal> dataSet, Context context,Map<String,Animal> map) {
        this.dataSet = dataSet;
        this.context = context;
        this.map=map;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_pet, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Animal animal=dataSet.get(position);
        Picasso.get().load(animal.getImage()).into(holder.imageView);
        holder.textViewName.setText(animal.getName());
        holder.textViewGender.setText(animal.getGender());
        holder.textViewType.setText(animal.getType());
        holder.textViewAge.setText(animal.getAge());
        holder.textViewNumber.setText(animal.getContactNumber());
        holder.textViewAddress.setText(animal.getAddress());
        holder.textViewColor.setText(animal.getColor());
        holder.textViewBreed.setText(animal.getBreed());

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Map.Entry entry: map.entrySet()){
                    Animal animal2=(Animal)entry.getValue();
                    if(animal.getName()==animal2.getName()){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query nameQuery = ref.child("Animals").orderByChild("name").equalTo(animal.getName());
                        nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("UserPetAdapter", "onCancelled", databaseError.toException());
                            }
                        });

                        String id=(String)entry.getKey();
                        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.d("UserPetAdapter", "onClick: "+id);

                        ref=FirebaseDatabase.getInstance().getReference("User-Animal/"+userId);
                        ref.child(id).removeValue();

                        FirebaseStorage mFirebaseStorage=FirebaseStorage.getInstance();
                        StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(animal.getImage());
                        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                                Log.d("UserPetAdapter", "onSuccess: deleted file");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                                Log.d("UserPetAdapter", "onFailure: did not delete file");
                            }
                        });
                    }

                    break;
                }
                Toast.makeText(context, "Your pet deleted successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button buttonDelete;
        TextView textViewName,textViewAge,textViewColor,textViewBreed,textViewType,textViewGender,textViewAddress,textViewNumber;
        ViewHolder(final View itemView) {
            super(itemView);
            textViewName=itemView.findViewById(R.id.textView_name);
            textViewAge=itemView.findViewById(R.id.textView_age);
            textViewBreed=itemView.findViewById(R.id.textView_breed);
            textViewColor=itemView.findViewById(R.id.textView_color);
            textViewAddress=itemView.findViewById(R.id.textView_address);
            textViewNumber=itemView.findViewById(R.id.textView_contactNumber);
            textViewType=itemView.findViewById(R.id.textView_type);
            textViewGender=itemView.findViewById(R.id.textView_gender);
            imageView=itemView.findViewById(R.id.animal_image);
            buttonDelete=itemView.findViewById(R.id.delete_pet);
        }

    }

    public interface OnItemClickListener{
        void onItemClick(Animal animal);
    }

    public void setOnItemClickListener(UserPetAdapter.OnItemClickListener listener){
        this.listener=listener;
    }

}

