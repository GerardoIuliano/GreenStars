package com.example.greenstars;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.io.ByteArrayOutputStream;
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
    private TextInputLayout tv_luogo,tv_descrizione;
    private TextInputEditText it_luogo,it_descrizione;
    private Bitmap immagine=null;
    private Button buttonConferma;
    private TextView error_img;
    private float dpi;
    private SharedPreferences.Editor editor;
    private SharedPreferences obj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_post);
        obj = getApplicationContext().getSharedPreferences("file",MODE_PRIVATE);
        editor = obj.edit();
        dpi = getApplicationContext().getResources().getDisplayMetrics().density;
        error_img=findViewById(R.id.error_img);
        buttonConferma=findViewById(R.id.buttonPost);
        buttonConferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(immagine==null){
                    error_img.setVisibility(View.VISIBLE);
                    return;
                }
                pubblica();
            }
        });
        tv_luogo=findViewById(R.id.luogo);
        tv_descrizione=findViewById(R.id.descrizione);
        it_luogo=findViewById(R.id.id_luogo);
        luogo=it_luogo.getText().toString();
        it_luogo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                luogo=it_luogo.getText().toString();
                Log.d("LIST_L","AFTER");
                if(luogo == null || luogo.equals("") || (luogo.trim()).equals("")){
                    Log.d("LIST_L_IN",luogo);
                    tv_luogo.setError("Campo non valido");
                    tv_luogo.setErrorEnabled(true);
                    buttonConferma.setEnabled(false);
                    buttonConferma.setBackgroundColor(Color.rgb(46,46,46));
                }else{
                    Log.d("LIST_L_OUT","FUORI");
                    tv_luogo.setError("");
                    tv_luogo.setErrorEnabled(false);
                    compilato();
                }
            }
        });
        it_descrizione=findViewById(R.id.id_descrizione);
        descrizione=it_descrizione.getText().toString();
        it_descrizione.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                descrizione=it_descrizione.getText().toString();
                Log.d("LIST_D","AFTER");
                if(descrizione == null || descrizione.equals("") || (descrizione.trim()).equals("")){
                    Log.d("LIST_D_IN",descrizione);
                    tv_descrizione.setError("Campo non valido");
                    tv_descrizione.setErrorEnabled(true);
                    buttonConferma.setEnabled(false);
                    buttonConferma.setBackgroundColor(Color.rgb(46,46,46));
                }else{
                    Log.d("LIST_D_OUT","FUORI");
                    tv_descrizione.setError("");
                    tv_descrizione.setErrorEnabled(false);
                    compilato();
                }
            }
        });
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
            immagine=Bitmap.createScaledBitmap(immagine, (int) ((immagine.getWidth()/dpi)*0.9), (int) ((immagine.getHeight()/dpi)*0.9), true);
            imageView.setImageBitmap(immagine);
            compilato();
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
            catch (IOException e) { }
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
    public void compilato(){
        if( !(descrizione == null || descrizione.trim().equals("")) && !(luogo == null || luogo.trim().equals("")) && immagine!=null){
            buttonConferma.setEnabled(true);
            buttonConferma.setBackgroundColor(Color.WHITE);
        }else{
            buttonConferma.setEnabled(false);
        }
    }
    public void pubblica(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        immagine.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        String imgString = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        editor.putString("IMMAGINE",imgString);
        luogo=it_luogo.getText().toString();
        editor.putString("LUOGO",luogo.toUpperCase());
        descrizione=it_descrizione.getText().toString();
        editor.putString("DESCRIZIONE",descrizione);
        editor.putBoolean("POST",true);
        editor.commit();
        onBackPressed();
    }
    public void back_to_bacheca(View view){
        onBackPressed();
    }

}
