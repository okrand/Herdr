package com.herdr.herdr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krando67 on 3/6/18.
 */

public class Browse extends AppCompatActivity {
    String TAG = "BROWSE";
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    Location myLoc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //Get location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        startLocationUpdates();

        // Functionality for Add New Herd (Plus) Button
        FloatingActionButton fabAdd = findViewById(R.id.add_button_browse);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startNewHerd = new Intent(Browse.this, NewHerd.class);
                Browse.this.startActivity(startNewHerd);
            }
        });

        getHerdsWithin15Miles();

        // Bottom Navigation
        BottomNavigationView navView = findViewById(R.id.browse_navigation);
        navView.setSelectedItemId(R.id.action_browse);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.action_herds):
                        Intent startMyHerds = new Intent(Browse.this, MyHerds.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        Browse.this.startActivity(startMyHerds);
                        break;
                    case (R.id.action_profile):
                        Intent startProfile = new Intent(Browse.this, com.herdr.herdr.Profile.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        Browse.this.startActivity(startProfile);
                        break;
                    case (R.id.action_settings):
                        Intent startSettings = new Intent(Browse.this, Settings.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        Browse.this.startActivity(startSettings);
                        break;
                }
                return false;
            }
        });
    }

    //Get Herds within 15 miles from Firebase
    private void getHerdsWithin15Miles() {
        final List<Herd> daList = new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("herds");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Herd h = dsp.getValue(Herd.class);
                    assert h != null;
                    Log.d("HERD", h.getTitle());
                    Location herdLoc = new Location("dummyprovider");
                    herdLoc.setLongitude(h.getPlace().getLongitude());
                    herdLoc.setLatitude(h.getPlace().getLatitude());
                    Log.d("DISTANCETO", h.getTitle() + herdLoc.distanceTo(myLoc));
                    if (herdLoc.distanceTo(myLoc) < 24140.2) //15 miles is 24140 meters
                        daList.add(h);
                }
                ListView herdsListView = findViewById(R.id.herds_browse);
                ArrayAdapter adapter = new ArrayAdapter<Herd>(Browse.this, R.layout.herd_list_item, R.id.herd_item_name, daList) {
                    @Override
                    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
                        final View view =  super.getView(position, convertView, parent);
                        TextView itemName = view.findViewById(R.id.herd_item_name);
                        itemName.setText(daList.get(position).getTitle());
                        return view;
                    }
                };
                herdsListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadHerd:onCancelled", databaseError.toException());
            }
        });
    }

    // Trigger new location updates at interval
    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            myLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (myLoc == null)
                myLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //Log.d("MYLOCATION", myLoc.toString());
        }
        // Define a listener that responds to location updates
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                myLoc = location;
                //Log.d("LOCATION", myLoc.toString());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, locationListener);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
