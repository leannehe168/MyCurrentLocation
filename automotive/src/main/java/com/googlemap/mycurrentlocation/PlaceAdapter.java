package com.googlemap.mycurrentlocation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private List<PlaceDetails> placeList;
    public PlaceAdapter(List<PlaceDetails> placeList) {
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        PlaceDetails place = placeList.get(position);
        holder.nameTextView.setText(place.getName());
        holder.addressTextView.setText(place.getAddress());
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    // Method to update data in RecyclerView
    public void updateData(List<PlaceDetails> newPlaceList) {
        placeList.clear();
        //placeList.addAll(newPlaceList);
        this.placeList = newPlaceList;
        notifyDataSetChanged();  // Notify that the data has changed and the RecyclerView should be updated
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView addressTextView;
        public PlaceViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.place_name);
            addressTextView = itemView.findViewById(R.id.place_address);
        }
    }

    // Method to update the list of places
    public void updatePlacesList(List<PlaceDetails> places) {
        this.placeList = places;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }
}