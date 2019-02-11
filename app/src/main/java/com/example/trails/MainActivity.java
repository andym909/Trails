package com.example.trails;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


//github commit testing, delete after
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openARview(View view) {
        Intent intent = new Intent(this, SimpleAR.class);
        startActivity(intent);
    }
}
