package com.example.greenstars;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mappa extends Fragment implements OnMapReadyCallback{

    private GoogleMap map;
    private SharedPreferences.Editor editor;
    private SharedPreferences obj;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mappa, container, false);
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
        String lat,lon;
        lat=obj.getString("LATITUDINE",null);
        lon=obj.getString("LONGITUDINE",null);
        if(lat!=null && lon!=null){
            LatLng nuovaMisurazione=new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
            map.addMarker(new MarkerOptions().position(nuovaMisurazione).title("New Marker").icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            map.moveCamera(CameraUpdateFactory.newLatLng(nuovaMisurazione));
        }
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
       // map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}