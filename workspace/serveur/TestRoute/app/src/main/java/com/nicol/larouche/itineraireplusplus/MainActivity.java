package com.nicol.larouche.itineraireplusplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    Button mapTrackRouteBtn;
    TextInputLayout departTextInputLayout;
    TextInputLayout premierChemTextInputLayout;
    TextInputLayout deuxiemeChemTextInputLayout;
    TextInputLayout troisiemeChemTextInputLayout;
    TextInputLayout destinationTextInputLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        departTextInputLayout = findViewById(R.id.textInputLayout1);
    //    premierChemTextInputLayout = findViewById(R.id.textInputLayout2);
    //    deuxiemeChemTextInputLayout = findViewById(R.id.textInputLayout3);
    //    troisiemeChemTextInputLayout = findViewById(R.id.textInputLayout4);
        destinationTextInputLayout = findViewById(R.id.textInputLayout5);
        mapTrackRouteBtn = findViewById(R.id.containedButton);


        mapTrackRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapTrackRouteIntent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(mapTrackRouteIntent);
            }
        });
    }
}