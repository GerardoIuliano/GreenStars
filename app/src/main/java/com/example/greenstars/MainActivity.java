package com.example.greenstars;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    public FrameLayout fr;
    public Fragment bacheca, mappa, misura;
    private SharedPreferences.Editor editor;
    private SharedPreferences obj;
    private String icon,luogo,descrizione;
    private boolean post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
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
            imageView.getLayoutParams().height = 215;
            TextView tv_luogo = findViewById(R.id.luogo0);
            tv_luogo.setText(luogo + "");
            TextView desc = findViewById(R.id.descrizione0);
            desc.setText(descrizione + "");
            LinearLayout linearLayout = findViewById(R.id.linear0);
            linearLayout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            linearLayout.setPadding(16, 16, 16, 16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 16, 0, 0);
            desc.setLayoutParams(params);
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
}
