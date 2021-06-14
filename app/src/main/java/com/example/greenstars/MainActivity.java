package com.example.greenstars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends Activity {

    FragmentManager fm = getFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    public FrameLayout fr;
    public Fragment bacheca, mappa, misura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

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

}
