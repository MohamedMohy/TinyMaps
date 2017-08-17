package com.example.mohamed.tinymaps;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    public static GoogleMap mMap;
    ArrayList<Polyline> polylines;
    Button dialogue_list;
    List<List<String>> markeees;
    public static ArrayList<Marker> markerPoints;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LocationManager locationManager;
    MarkerDataSource markerDataSource;
    String provider;
    static HashMap<Integer, Marker> hashmap;
    
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location")
                        .setMessage("ALLOW TO ACCESS LOCATION!!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        provider = locationManager.getBestProvider(new Criteria(), false);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                }
                return;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Request location updates:
                locationManager.requestLocationUpdates(provider, 400, 1, this);
            }
        }
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
            Intent intent = new Intent(this, MarkersActivity.class);
            ArrayList<String> goArray = parseArray(markerPoints);
            intent.putExtra("markers", goArray);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hashmap = new HashMap<>();
        polylines = new ArrayList<>();
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Your Map");
        setSupportActionBar(toolbar);
        markerDataSource = new MarkerDataSource(getApplicationContext());
        markerDataSource.open();
        markeees = markerDataSource.getAllMarkers();
        markerDataSource.close();
        markerPoints = new ArrayList<Marker>();
        SupportMapFragment fm = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        dialogue_list = (Button) findViewById(R.id.toolbar_overflow_menu_button);
        dialogue_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialoge_spinner, null);
                mBuilder.setTitle("Select start and finish");
                final Spinner spinnersrc = (Spinner) mView.findViewById(R.id.spinner1);
                final Spinner spinnerdest = (Spinner) mView.findViewById(R.id.spinner2);
                ArrayList lista = new ArrayList<>();
                for (Marker mark : markerPoints) {
                    lista.add(mark.getTitle() + '\n' + mark.getPosition().toString());
                }
                ArrayAdapter adapter = new ArrayAdapter(MapsActivity.this, android.R.layout.simple_spinner_item, lista);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnersrc.setAdapter(adapter);
                spinnerdest.setAdapter(adapter);
                mBuilder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String s = spinnersrc.getSelectedItem().toString();

                        s = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
                        String[] lats = s.split(",");
                        double latsrc = Double.parseDouble(lats[0]);
                        double lonsrc = Double.parseDouble(lats[1]);
                        String s2 = spinnerdest.getSelectedItem().toString();
                        s2 = s2.substring(s2.indexOf("(") + 1, s2.indexOf(")"));
                        String[] latsdest = s2.split(",");
                        double lades = Double.parseDouble(latsdest[0]);
                        double londest = Double.parseDouble(latsdest[1]);
                        String url = getDirectionsUrl(new LatLng(latsrc, lonsrc), new LatLng(lades, londest));
                        DownloadTask downloadTask = new DownloadTask();
                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);


                    }
                }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                mBuilder.setView(mView);
                AlertDialog dia = mBuilder.create();
                dia.show();


            }
        });

    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }
    };
    static String smarkersname = "";

    public void showDialog(String text, final Marker marker) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        final LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialouge, null))
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog f = (Dialog) dialog;
                        EditText editText = f.findViewById(R.id.markers_name);
                        smarkersname = editText.getText().toString();
                        renameMarker(marker, smarkersname);
                    }
                })
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    public ArrayList parseArray(ArrayList<Marker> markers) {
        ArrayList<String> array = new ArrayList<>();
        String temp = "";
        for (Marker mark : markers) {
            temp = mark.getTitle() + "\n" + mark.getPosition().toString();
            array.add(temp);
        }
        return array;
    }

    public void renameMarker(Marker marker, String ss) {
        marker.setTitle(ss);
        markerDataSource.open();
        ArrayList s = new ArrayList();
        s.add(marker.getTitle());
        s.add(marker.getPosition().toString());
        markerDataSource.addMarkers(s);
        markerDataSource.close();
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (int i = 0; i < markeees.size(); i++) {
            String s = markeees.get(i).get(1).substring(markeees.get(i).get(1).indexOf("(") + 1, markeees.get(i).get(1).indexOf(")"));
            String[] lats = s.split(",");
            double lat = Double.parseDouble(lats[0]);
            double lon = Double.parseDouble(lats[1]);

            Marker ma = MapsActivity.mMap.addMarker(new MarkerOptions().title(markeees.get(i).get(0)).position(new LatLng(lat, lon)));
            ArrayList los = new ArrayList();
            los.add(markeees.get(i).get(0));
            los.add(markeees.get(i).get(1));
            markerPoints.add(ma);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String name = marker.getTitle();
                for (Marker mark : markerPoints) {
                    if (mark.getTitle() == name)
                        markerPoints.remove(mark);
                }
                marker.remove();
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Marker marker = mMap.addMarker(new MarkerOptions().position(point));
                showDialog("", marker);
                markerPoints.add(marker);
                hashmap.put(markerPoints.indexOf(marker), marker);

            }

        });
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onLocationChanged(Location location) {
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();

        Log.i("Location info: Lat", lat.toString());
        Log.i("Location info: Lng", lng.toString());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                JsonParser parser = new JsonParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(9);
                lineOptions.color(Color.RED);
            }


            try {
                for (Polyline line : polylines) {
                    line.remove();
                }
                polylines.add(mMap.addPolyline(lineOptions));

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MapsActivity.this, "Cannot find route!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
