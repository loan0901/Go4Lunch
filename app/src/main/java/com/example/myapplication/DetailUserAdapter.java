package com.example.myapplication;

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

import java.util.ArrayList;
import java.util.List;

public class DetailUserAdapter extends RecyclerView.Adapter<DetailUserAdapter.UserDetailViewHolder> {

    private List<User> users = new ArrayList<>();
    private final RequestManager glide;

    public DetailUserAdapter(RequestManager glide) {
        this.glide = glide;
    }

    @NonNull
    @Override
    public UserDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmate, parent, false);
        return new UserDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDetailViewHolder holder, int position) {
        // Get the current user
        User user = users.get(position);

        // Load user's profile picture using Glide
        if (user.getPhotoUrl() != null){
            Uri photoUri = Uri.parse(user.getPhotoUrl());
            glide.load(photoUri).into(holder.imageViewProfilePicture);
        } else {
            // Set default image if no photo URL is available
            // TODO : ajouter image par defaut
            holder.imageViewProfilePicture.setImageResource(R.drawable.ic_launcher_background);
        }

        // Set the user's name and message
        holder.textViewName.setText(user.getUserName() + " mange ici !");
    }

    @Override
    public int getItemCount() {
        // Return the total number of users
        return users.size();
    }

    // Method to update the list of users
    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    // ViewHolder class to hold references to the views in each item
    static class UserDetailViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private ImageView imageViewProfilePicture;

        public UserDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            textViewName = itemView.findViewById(R.id.restaurantChoice);
            imageViewProfilePicture = itemView.findViewById(R.id.workmateImageView);
        }
    }
}
