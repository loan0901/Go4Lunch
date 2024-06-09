package com.example.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.CustomOpeningHours;
import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.service.AddressService;
import com.example.myapplication.service.ClosingTimeService;
import com.example.myapplication.service.DistanceService;
import com.example.myapplication.service.FirestoreUtils;
import com.example.myapplication.service.PhotoService;
import com.example.myapplication.service.RatingService;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

    private List<CustomPlace> places = new ArrayList<>();
    private PhotoService photoService;
    private LatLng currentUserLatLng;
    private OnItemClickListener listener;
    private SharedPlaceViewModel sharedPlaceViewModel;
    private RatingService ratingService;

    // Constructor to initialize the adapter
    public PlaceListAdapter(OnItemClickListener listener, String apiKey, SharedPlaceViewModel sharedPlaceViewModel) {
        this.listener = listener;
        this.photoService = new PhotoService(apiKey);
        this.sharedPlaceViewModel = sharedPlaceViewModel;
        this.ratingService = new RatingService(sharedPlaceViewModel);
    }

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(CustomPlace place);
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {

        CustomPlace place = places.get(position);

        holder.name.setText(place.displayName.value);
        holder.address.setText(AddressService.getStreetAndNumber(place.address));
        holder.openHour.setText(ClosingTimeService.displayClosingTime(place.openingHours));

        // Calculate and display the distance to the place
        if (currentUserLatLng != null && place.location != null) {
            holder.distanceToPlace.setText(DistanceService.calculateDistance(currentUserLatLng, place.location));
        } else {
            holder.distanceToPlace.setText(" ");
        }

        // Fetch and set the image for the place
        photoService.displayFirstPhoto(place, holder.imageView);

        // Display the number of coworkers if any
        if (sharedPlaceViewModel.getRestaurantById(place.placeId).getUserIdSelected() == null || sharedPlaceViewModel.getRestaurantById(place.placeId).getUserIdSelected().isEmpty()){
            holder.iconCoworker.setVisibility(View.GONE);
            holder.textViewCoworkerNumber.setVisibility(View.GONE);
        } else {
            int coworkerNumber = sharedPlaceViewModel.getRestaurantById(place.placeId).getUserIdSelected().size();
            holder.iconCoworker.setVisibility(View.VISIBLE);
            holder.textViewCoworkerNumber.setVisibility(View.VISIBLE);
            holder.textViewCoworkerNumber.setText("(" + coworkerNumber + ")");
        }

        // Display the rating stars
        holder.oneStars.setVisibility(View.GONE);
        holder.twoStars.setVisibility(View.GONE);
        holder.threeStars.setVisibility(View.GONE);
        ratingService.computeRating(place.placeId, new RatingService.OnRatingComputedListener() {
            @Override
            public void onRatingComputed(int rating) {
                if (rating != 0) {
                    if (rating >= 1) {
                        holder.oneStars.setVisibility(View.VISIBLE);
                    }
                    if (rating >= 2) {
                        holder.twoStars.setVisibility(View.VISIBLE);
                    }
                    if (rating == 3) {
                        holder.threeStars.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                // Handle error
                Log.e("RatingError", "Error computing rating", e);
            }
        });

        // Set the click listener for the item
        holder.itemView.setOnClickListener(v -> listener.onItemClick(place));
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        if (places != null){
            return places.size();
        } else {
            return 0;
        }
    }

    // Updates the list of places and notifies the adapter
    public void setPlaces(List<CustomPlace> places, LatLng currentUserLatLng) {
        this.places = places;
        this.currentUserLatLng = currentUserLatLng;
        notifyDataSetChanged();
    }


    // ViewHolder class to hold the item views
    static class PlaceViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView address;
        TextView openHour;
        TextView distanceToPlace;
        ImageView imageView;
        ImageView iconCoworker;
        TextView textViewCoworkerNumber;
        ImageView oneStars;
        ImageView twoStars;
        ImageView threeStars;

        // Constructor to initialize the item views
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.placeName);
            address = itemView.findViewById(R.id.address);
            openHour = itemView.findViewById(R.id.openHour);
            distanceToPlace = itemView.findViewById(R.id.distanceToPlace);
            imageView = itemView.findViewById(R.id.placeImageView);
            iconCoworker = itemView.findViewById(R.id.iconCoWorker);
            textViewCoworkerNumber = itemView.findViewById(R.id.textViewCoWorkerNumber);
            oneStars = itemView.findViewById(R.id.oneStarsXml);
            twoStars = itemView.findViewById(R.id.twoStarsXml);
            threeStars = itemView.findViewById(R.id.threeStarsXml);
        }
    }
}
