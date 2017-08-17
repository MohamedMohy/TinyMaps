package com.example.mohamed.tinymaps;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    String Data;
    String[] parsedData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.Details);
        setSupportActionBar(toolbar);
        Data = getIntent().getStringExtra("data");
        parsedData =parseData(Data);
        TextView name = (TextView)findViewById(R.id.Details_name);
        TextView latlng = (TextView)findViewById(R.id.Details_latlng);
        name.setText(parsedData[0]);
        latlng.setText(parsedData[1]);

    }

    private String[] parseData(String s){
        String[] parsedData;
        parsedData = s.split("\n");
        return parsedData;
    }

}
