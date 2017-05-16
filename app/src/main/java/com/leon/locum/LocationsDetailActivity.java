package com.leon.locum;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class LocationsDetailActivity extends AppCompatActivity{
    private TextView textTitle, textAddress;

    // Initializing item in list
   // @Override
    protected void OnCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_list);
        textTitle = (TextView) findViewById(R.id.textTitle);
        textAddress = (TextView) findViewById(R.id.textAddress);
        Locations loc = getIntent().getParcelableExtra("location");
        if(loc != null){
            textTitle.setText(loc.getName());
            textAddress.setText(loc.getAddress());
        }
    }
}
