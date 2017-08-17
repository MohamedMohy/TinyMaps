package com.example.mohamed.tinymaps;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarkersActivity extends AppCompatActivity implements Serializable {
    ArrayList<String> markersList;
    MarkerDataSource markerDataSource;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markers);
        markerDataSource = new MarkerDataSource(getApplicationContext());
        markersList = (ArrayList) getIntent().getSerializableExtra("markers");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.Markers);
        setSupportActionBar(toolbar);
        final ArrayAdapter markersAdabter = new ArrayAdapter(this, R.layout.list_item_marker, markersList);
        ListView listView = (ListView) findViewById(R.id.markers_list);
        listView.setAdapter(markersAdabter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MarkersActivity.this, DetailsActivity.class);
                String ss = markersList.get(i);
                String[] s = ss.split("\n");
                Toast.makeText(MarkersActivity.this, s[1].toString(), Toast.LENGTH_SHORT).show();
                intent.putExtra("data", s[0].toString() + '\n' + s[1].toString());
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                markerDataSource.open();
                markerDataSource.deleteEntry(i);
                markerDataSource.close();
                markersList.remove(i);
                MapsActivity.markerPoints.remove(i);
                markersAdabter.notifyDataSetChanged();
                Marker ma = MapsActivity.hashmap.get(i);
                try {
                    ma.remove();
                    MapsActivity.hashmap.remove(ma);
                } catch (NullPointerException e) {

                }


                return false;
            }
        });
    }

    ArrayList<String> parsed_array(String s) {
        String[] l;
        if (s.startsWith("["))
            s = s.substring(1, s.length() - 1);
        l = s.split("\n");
        ArrayList o = new ArrayList();
        o.add(l[0]);
        o.add(l[1]);
        return o;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings_1) {
            markerDataSource.clearDatabase();
        }
        return super.onOptionsItemSelected(item);
    }
}
