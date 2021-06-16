package com.example.greenstars;
import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

public class AddMisura extends Activity {

    private SharedPreferences.Editor editor;
    private SharedPreferences obj;
    private RadioGroup radioGroup;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_misura);
        obj = getApplicationContext().getSharedPreferences("file", MODE_PRIVATE);
        editor = obj.edit();
        radioGroup = findViewById(R.id.radioGroup);
        button = findViewById(R.id.confermaMisura);
        //RECUPERO FOTO
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                button.setEnabled(true);
            }
        });
        String icon = obj.getString("SCATTO", null);
        Bitmap img = null;
        if (icon != null) {
            byte[] decode = Base64.decode(icon.getBytes(), 1);
            img = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            Log.d("DEBUG", "FINE");
        }
        //CALCOLO INDICE LUMINOSITA
        int sqm = calculateBrightness(img);
        editor.putInt("SQM",sqm);
        editor.commit();
        //SET INDICE (0-255)
        TextView textView = findViewById(R.id.indeceSQM);
        if (textView != null)
            textView.setText("SQM: " + sqm);
        //SET IMAGE
        ImageView imageView = findViewById(R.id.imgScatto);
        if (imageView != null) {
            //img=Bitmap.createScaledBitmap(img,600,900,true);
            imageView.setImageBitmap(img);
        }


    }
    /*
 Calculates the estimated brightness of an Android Bitmap.
 pixelSpacing tells how many pixels to skip each pixel. Higher values result in better performance, but a more rough estimate.
 When pixelSpacing = 1, the method actually calculates the real average brightness, not an estimate.
 This is what the calculateBrightness() shorthand is for.
 Do not use values for pixelSpacing that are smaller than 1.
 */
    public int calculateBrightnessEstimate(android.graphics.Bitmap bitmap, int pixelSpacing) {
        int R = 0;
        int G = 0;
        int B = 0;
        if (bitmap != null) {
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            int n = 0;
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            for (int i = 0; i < pixels.length; i += pixelSpacing) {
                int color = pixels[i];
                R += Color.red(color);
                G += Color.green(color);
                B += Color.blue(color);
                n++;
            }
            return (R + B + G) / (n * 3);
        }
        return -1;
    }

    public int calculateBrightness(Bitmap bitmap) {
        return calculateBrightnessEstimate(bitmap, 1);
    }


    public void addMisura(View view) {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Double latitudine,longitudine;
        latitudine=location.getLatitude();
        longitudine=location.getLongitude();
        editor.putString("LATITUDINE",latitudine+"");
        editor.putString("LONGITUDINE",longitudine+"");
        editor.commit();
        onBackPressed();
        Toast.makeText(getApplicationContext(), "MISURAZIONE EFFETTUATA CON SUCCESSSO", Toast.LENGTH_LONG).show();
    }

    public void back_to_misura(View view){
        onBackPressed();
    }
}

