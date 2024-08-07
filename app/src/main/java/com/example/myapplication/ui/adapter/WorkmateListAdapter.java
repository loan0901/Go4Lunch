package com.example.myapplication.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class WorkmateListAdapter extends RecyclerView.Adapter<WorkmateListAdapter.WorkmateViewHolder> {

    private List<User> users = new ArrayList<>();
    private final RequestManager glide;
    private OnItemClickListener listener;
    private Context context;

    // Constructor to initialize Glide and the click listener
    public WorkmateListAdapter(Context context, RequestManager glide, OnItemClickListener listener) {
        this.glide = glide;
        this.listener = listener;
        this.context = context;
    }

    // Interface for item click events
    public interface OnItemClickListener {
        void onItemClick(String restaurantId);
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmate, parent, false);
        return new WorkmateViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {
        // Get the user at the current position
        User user = users.get(position);

        // display profile picture in imageView
        if (user.getPhotoUrl() != null){
            Uri photoUri = Uri.parse(user.getPhotoUrl());
            glide.load(photoUri).into(holder.imageViewProfilePicture);
        } else {
            holder.imageViewProfilePicture.setImageResource(R.drawable.baseline_account_circle_24);
        }

        // display name and chosen restaurant
        if (user.getSelectedRestaurantName() != null) {
            String textRestaurantChoice = user.getUserName() + context.getString(R.string.eat_at) + user.getSelectedRestaurantName();
            holder.restaurantName.setText(textRestaurantChoice);
            holder.restaurantName.setTextColor(Color.argb(255, 0, 0, 0)); // black
        } else {
            String textNoChoice = user.getUserName() + context.getString(R.string.hasnt_chosen_yet);
            holder.restaurantName.setText(textNoChoice);
            holder.restaurantName.setTextColor(Color.argb(128, 128, 128, 128)); // Gray with transparency
        }

        // Set the click listener for the item view
        holder.itemView.setOnClickListener(view -> listener.onItemClick(user.getSelectedRestaurantId()));
    }

    // Return the size of the dataset
    @Override
    public int getItemCount() {
        return users.size();
    }

    // Method to update the list of users and notify the adapter
    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }


    // Provide a reference to the views for each data item
    public static class WorkmateViewHolder extends RecyclerView.ViewHolder {

        TextView restaurantName;
        ImageView imageViewProfilePicture;

        // Constructor to initialize the view holder
        public WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantName = itemView.findViewById(R.id.restaurantChoice);
            imageViewProfilePicture = itemView.findViewById(R.id.workmateImageView);
        }
    }
}
