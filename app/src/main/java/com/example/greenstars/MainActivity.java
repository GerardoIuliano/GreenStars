package com.example.greenstars;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.tabs.TabLayout;
import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PHOTO_REQUEST_CODE = 11;
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    public FrameLayout fr;
    public Fragment bacheca, mappa, misura;
    private SharedPreferences.Editor editor;
    private SharedPreferences obj;
    private String icon,luogo,descrizione;
    private boolean post;
    private float dpi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        dpi = getApplicationContext().getResources().getDisplayMetrics().density;
        obj = getApplicationContext().getSharedPreferences("file",MODE_PRIVATE);
        editor = obj.edit();
        icon = obj.getString("IMMAGINE",null);
        luogo = obj.getString("LUOGO",null);
        descrizione = obj.getString("DESCRIZIONE",null);

        Bitmap img;
        if(icon!=null) {
            byte[] decode = Base64.decode(icon.getBytes(), 1);
            img = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            ImageView imageView = findViewById(R.id.imgView);
            Log.d("PUBBLICA_MAIN",icon);
            if(imageView!=null)
                imageView.setImageBitmap(img);
        }

        fr= findViewById(R.id.id_container);
        bacheca = new Bacheca();
        ft.add(fr.getId(),bacheca,null);
        ft.commit();

        TabLayout tab = findViewById(R.id.id_tabLayout);
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ft = fm.beginTransaction();
                Log.d("SELECTED",""+tab.getText());

                //INSERISCI FRAGMENT BACHECA
                if(tab.getText().equals("BACHECA")){
                    bacheca = new Bacheca();
                    ft.add(fr.getId(),bacheca,null);
                }

                //INSERISCI FRAGMENT MAPPA
                if(tab.getText().equals("MAPPA")){
                    mappa = new Mappa();
                    ft.add(fr.getId(),mappa,null);
                }

                //INSERISCI FRAGMMENT MISURA
                if(tab.getText().equals("MISURA")){
                    misura = new Misura();
                    ft.add(fr.getId(),misura,null);
                }
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ft = fm.beginTransaction();
                Log.d("UNSELECTED",""+tab.getText());

                //RIMUOVI FRAGMENT BACHECA
                if(tab.getText().equals("BACHECA")){
                   ft.remove(bacheca);
                }

                //RIMUOVI FRAGMENT MAPPA
                if(tab.getText().equals("MAPPA")){
                    ft.remove(mappa);
                }

                //RIMUOVI FRAGMMENT MISURA
                if(tab.getText().equals("MISURA")){
                    ft.remove(misura);
                }
                ft.commit();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d("RESELECTED",""+tab.getText());
            }
        });
    }
    public void addPost(View view){
        Intent intent = new Intent(MainActivity.this,AddPost.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        post=obj.getBoolean("POST",false);
        if(post) {
            Log.d("DEBUG", "INIZIO RESTART");
            icon = obj.getString("IMMAGINE", null);
            luogo = obj.getString("LUOGO", null);
            descrizione = obj.getString("DESCRIZIONE", null);
            Bitmap img = null;
            if (icon != null) {
                byte[] decode = Base64.decode(icon.getBytes(), 1);
                img = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                Log.d("DEBUG", "FINE");
            }
            ImageView imageView = (ImageView) findViewById(R.id.img0);
            if (imageView != null)
                imageView.setImageBitmap(img);
            imageView.getLayoutParams().height = (int) (194*dpi);
            TextView tv_luogo = findViewById(R.id.luogo0);
            tv_luogo.setText(luogo + "");
            TextView desc = findViewById(R.id.descrizione0);
            desc.setText(descrizione + "");
            LinearLayout linearLayout = findViewById(R.id.linear0);
            linearLayout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            linearLayout.setPadding(16, 16, 16, 16);
            linearLayout.setBackgroundColor(Color.BLACK);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 16, 0, 0);
            desc.setLayoutParams(params);
            linearLayout = findViewById(R.id.linear00);
            linearLayout.getLayoutParams().height=LinearLayout.LayoutParams.WRAP_CONTENT;
            Toast.makeText(getApplicationContext(), "POST PUBBLICATO CON SUCCESSSO", Toast.LENGTH_LONG).show();
            post=false;
            editor.putBoolean("POST",false);
            editor.commit();
        }
    }

    public void info(View view){
        Intent intent = new Intent(MainActivity.this,Info.class);
        startActivity(intent);
    }

    public void apriFotocamera(View view){
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PHOTO_REQUEST_CODE)
        {
            Intent intent = new Intent(MainActivity.this,AddMisura.class);
            startActivity(intent);
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bp.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
            String imgString = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            editor.putString("SCATTO",imgString);
            editor.commit();
        }
    }
}
