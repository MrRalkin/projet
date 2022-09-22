package com.nicol.larouche.itineraireplusplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

        public List<Route> parse(String routesJSONString) throws Exception {
            try {
                List<Route> routeList = new ArrayList<Route>();
                final JSONObject jSONObject = new JSONObject(routesJSONString);
                JSONArray routeJSONArray = jSONObject.getJSONArray(ROUTES);
                Route route;
                JSONObject routesJSONObject;
                for (int m = 0; m < routeJSONArray.length(); m++) {
                    route = new Route(context);
                    routesJSONObject = routeJSONArray.getJSONObject(m);
                    JSONArray legsJSONArray;
                    route.setSummary(routesJSONObject.getString(SUMMARY));
                    legsJSONArray = routesJSONObject.getJSONArray(LEGS);
                    JSONObject legJSONObject;
                    Leg leg;
                    JSONArray stepsJSONArray;
                    for (int b = 0; b < legsJSONArray.length(); b++) {
                        leg = new Leg();
                        legJSONObject = legsJSONArray.getJSONObject(b);
                        leg.setDistance(new Distance(legJSONObject.optJSONObject(DISTANCE).optString(TEXT), legJSONObject.optJSONObject(DISTANCE).optLong(VALUE)));
                        leg.setDuration(new Duration(legJSONObject.optJSONObject(DURATION).optString(TEXT), legJSONObject.optJSONObject(DURATION).optLong(VALUE)));
                        stepsJSONArray = legJSONObject.getJSONArray(STEPS);
                        JSONObject stepJSONObject, stepDurationJSONObject, legPolyLineJSONObject, stepStartLocationJSONObject, stepEndLocationJSONObject;
                        Step step;
                        String encodedString;
                        LatLng stepStartLocationLatLng, stepEndLocationLatLng;
                        for (int i = 0; i < stepsJSONArray.length(); i++) {
                            stepJSONObject = stepsJSONArray.getJSONObject(i);
                            step = new Step();
                            JSONObject stepDistanceJSONObject = stepJSONObject.getJSONObject(DISTANCE);
                            step.setDistance(new Distance(stepDistanceJSONObject.getString(TEXT), stepDistanceJSONObject.getLong(VALUE)));
                            stepDurationJSONObject = stepJSONObject.getJSONObject(DURATION);
                            step.setDuration(new Duration(stepDurationJSONObject.getString(TEXT), stepDurationJSONObject.getLong(VALUE)));
                            stepEndLocationJSONObject = stepJSONObject.getJSONObject(END_LOCATION);
                            stepEndLocationLatLng = new LatLng(stepEndLocationJSONObject.getDouble(LATITUDE), stepEndLocationJSONObject.getDouble(LONGITUDE));
                            step.setEndLocation(stepEndLocationLatLng);
                            step.setHtmlInstructions(stepJSONObject.getString(HTML_INSTRUCTION));
                            legPolyLineJSONObject = stepJSONObject.getJSONObject(POLYLINE);
                            encodedString = legPolyLineJSONObject.getString(POINTS);
                            step.setPoints(decodePolyLines(encodedString));
                            stepStartLocationJSONObject = stepJSONObject.getJSONObject(START_LOCATION);
                            stepStartLocationLatLng = new LatLng(stepStartLocationJSONObject.getDouble(LATITUDE), stepStartLocationJSONObject.getDouble(LONGITUDE));
                            step.setStartLocation(stepStartLocationLatLng);
                            leg.addStep(step);
                        }
                        route.addLeg(leg);
                    }
                    routeList.add(route);
                }
                return routeList;
            } catch (Exception e) {
                throw e;
            }
    }
}