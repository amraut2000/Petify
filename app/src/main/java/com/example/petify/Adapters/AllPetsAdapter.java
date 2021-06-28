package com.example.petify.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petify.Animal;
import com.example.petify.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AllPetsAdapter extends RecyclerView.Adapter<AllPetsAdapter.ViewHolder> {

    private List<Animal> dataSet;
    private Context context;

    public AllPetsAdapter(List<Animal> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet, parent, false);
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
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
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
        }

    }

}
