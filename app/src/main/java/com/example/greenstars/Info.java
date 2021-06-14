package com.example.greenstars;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class Info extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info);
    }

    public void back(View view){
        onBackPressed();
    }
}
