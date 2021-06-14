package com.example.greenstars;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.app.ActivityCompat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddPost extends Activity {
    private static final int RESULT_LOAD_IMAGE = 0;
    private String providerId = LocationManager.GPS_PROVIDER;
    private Geocoder geo = null;
    private LocationManager locationManager = null;
    private static final int MIN_DIST = 20;
    private static final int MIN_PERIOD = 30000;
    private String luogo,descrizione;
    private Bitmap immagine;
    private Button buttonConferma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_post);
        buttonConferma=findViewById(R.id.buttonPost);
    }

    public void apriGalleria(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            InputStream inputStream = null;

            if (ContentResolver.SCHEME_CONTENT.equals(selectedImage.getScheme())) {
                try {
                    inputStream = this.getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                if (ContentResolver.SCHEME_FILE.equals(selectedImage.getScheme())) {
                    try {
                        inputStream = new FileInputStream(selectedImage.getPath());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            immagine = BitmapFactory.decodeStream(inputStream);
            ImageView imageView = findViewById(R.id.imgView);
            imageView.setImageBitmap(immagine);
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            // attivo GPS su dispositivo
           // updateText(R.id.enabled, "TRUE");
        }

        @Override
        public void onProviderDisabled(String provider) {
            // disattivo GPS su dispositivo
            //updateText(R.id.enabled, "FALSE");
        }

        @Override
        public void onLocationChanged(Location location) {
            //updateGUI(location);
        }
    };

    private void updateGUI(Location location) {
        Date timestamp = new Date(location.getTime());
        //updateText(R.id.timestamp, timestamp.toString());
        double latitude = location.getLatitude();
        //updateText(R.id.latitude, String.valueOf(latitude));
        double longitude = location.getLongitude();
        //updateText(R.id.longitude, String.valueOf(longitude));
        new AddressSolver().execute(location);
    }

    private void updateText(int id, String text) {
        TextView textView = (TextView) findViewById(id);
        textView.setText(text);
    }


    @Override
    protected void onResume() {
        super.onResume();
        geo = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location!=null)
            updateGUI(location);
        /*
        if (locationManager!=null && locationManager.isProviderEnabled(providerId))
            updateText(R.id.enabled, "TRUE");
        else
            updateText(R.id.enabled, "FALSE");
        */
        locationManager.requestLocationUpdates(providerId, MIN_PERIOD,MIN_DIST, locationListener);

    }
    @Override
    protected void onPause()
    {
        super.onPause();
        if (locationManager!=null && locationManager.isProviderEnabled(providerId))
            locationManager.removeUpdates(locationListener);
    }

    private class AddressSolver extends AsyncTask<Location, Void, String>
    {
        @Override
        protected String doInBackground(Location... params)
        {
            Location pos=params[0];
            double latitude = pos.getLatitude();
            double longitude = pos.getLongitude();
            List<Address> addresses = null;
            try
            {
                addresses = geo.getFromLocation(latitude, longitude, 1);
            }
            catch (IOException e)
            {
            }
            if (addresses!=null)
            {
                if (addresses.isEmpty())
                {
                    Log.d("ADDRESS_NULL",addresses+"");
                    return null;
                }
                else {
                    if (addresses.size() > 0)
                    {
                        StringBuffer address=new StringBuffer();
                        Log.d("ADDRESS DONE",addresses.get(0).getAddressLine(0));
                        Address tmp=addresses.get(0);
                        for (int y=0;y<tmp.getMaxAddressLineIndex();y++)
                            address.append(tmp.getAddressLine(y)+"\n");

                        return addresses.get(0).getAddressLine(0);

                    }
                }
            }
            Log.d("ADDRESS_NOTNULL",addresses+"");
            return null;
        }
        @Override
        protected void onPostExecute(String result)
        {
            Log.d("RESULT_",result);
            if (result!=null)
                updateText(R.id.id_luogo, result);

            else
                updateText(R.id.id_luogo, "N.A.");
        }
    }

    public boolean isFilled(){
        if(luogo!=null && descrizione!=null && immagine!=null) {
            buttonConferma.setEnabled(true);
            return true;
        }else
            return false;
    }

    public void verificaInput(){

    }
}
