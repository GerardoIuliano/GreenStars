package com.example.greenstars;


import android.content.Context;
import android.content.SharedPreferences;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.gms.common.api.Status;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mappa extends Fragment implements OnMapReadyCallback{

    private GoogleMap map;

    private PlacesClient placesClient;
    private float color;
    private SharedPreferences.Editor editor;
    private SharedPreferences obj;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mappa, container, false);


        String apiKey = getString(R.string.places_key);
        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(this.getContext());
        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),10));
                System.out.println(place.getLatLng().toString());
               // Toast.makeText(getActivity().getApplicationContext(), place.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Toast.makeText(getActivity().getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        obj = getContext().getSharedPreferences("file", Context.MODE_PRIVATE);
        editor = obj.edit();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        String lat,lon;
        lat=obj.getString("LATITUDINE",null);
        lon=obj.getString("LONGITUDINE",null);
        int sqm=obj.getInt("SQM",1);
        if(lat!=null && lon!=null){
            LatLng nuovaMisurazione=new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
            if(sqm>=0 && sqm<81)
                color=BitmapDescriptorFactory.HUE_YELLOW;
            if(sqm>=81 && sqm<165)
                color=BitmapDescriptorFactory.HUE_ORANGE;
            if(sqm>=165 && sqm<256)
                color=BitmapDescriptorFactory.HUE_RED;
            map.addMarker(new MarkerOptions().position(nuovaMisurazione).title("New Marker").icon(BitmapDescriptorFactory
                    .defaultMarker(color)));
            map.moveCamera(CameraUpdateFactory.newLatLng(nuovaMisurazione));
        }
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        Resources res = getResources();
        TypedArray latitude = res.obtainTypedArray(R.array.lat);
        TypedArray longitude = res.obtainTypedArray(R.array.lon);
        TypedArray colori = res.obtainTypedArray(R.array.color);
        LatLng luogo;
        for(int i=0;i<latitude.length();i++){
            luogo = new LatLng(Double.parseDouble(latitude.getString(i)), Double.parseDouble(longitude.getString(i)));
            switch (Integer.parseInt(colori.getString(i))){
                case 1: color=BitmapDescriptorFactory.HUE_YELLOW;break;
                case 2: color=BitmapDescriptorFactory.HUE_ORANGE;break;
                case 3: color=BitmapDescriptorFactory.HUE_RED;break;
            }
            map.addMarker(new MarkerOptions().position(luogo).icon(BitmapDescriptorFactory
                    .defaultMarker(color)));
        }

    }
}
//48.7965953656776, 2.3846751705448317 Parigi
//40.45180480370188, -3.723722556675278 Madrid
//48.11498936318354, 11.601792343981916 Monaco
//38.07171606881287, 23.74100338422546 Atene
///53.402212795870966, -2.9393904746162396 Liverpool
//45.51082029014112, 9.162867204421842 Milano
//35.76590636472205, 139.72364044818207 Tokio
//55.67318986816773, 37.817751983016564 Mosca