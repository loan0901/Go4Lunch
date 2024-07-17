package com.example.myapplication.service;

import android.util.Log;
import android.widget.ImageView;

import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

public class PhotoService {

    private final String apikey;

    // Constructor for PhotoService.
    public PhotoService(String apikey) {
        this.apikey = apikey;
    }

    /**
     * Displays the first photo of a place in the provided ImageView.
     *
     * @param place The place whose photo is to be displayed.
     * @param imageView The ImageView in which to display the photo.
     */
    public void displayFirstPhoto(CustomPlace place, ImageView imageView) {
        if (place.photoMetadatas != null && !place.photoMetadatas.isEmpty()) {
            String photoReference = place.photoMetadatas.get(0).photoReference;
            String photoUrl = getPhotoUrl(photoReference);
            Picasso.get().load(photoUrl).into(imageView);

        } else {
            // Handle the case where there are no photos available
            Log.i("Photo", "No photos available for place ID: " + place.placeId);
            // set a default image if no photo is available
            imageView.setImageResource(R.drawable.baseline_hide_image_24); // display default image
        }
    }

    //Constructs the URL to retrieve a photo using the photo reference
    private String getPhotoUrl(String photoReference) {
        String baseUrl = "https://places.googleapis.com/v1/";
        int maxWidth = 400;  // Adjust the maximum width
        return baseUrl + photoReference + "/media?key=" + apikey + "&maxWidthPx=" + maxWidth;
    }
}
