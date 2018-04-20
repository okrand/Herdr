package com.herdr.herdr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    Boolean gotHerd = false;
    String TAG = "BROWSE";
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    private static final int NEW_HERD = 9;
    Location myLoc;
    ArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);
        //Get location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        else {
            startLocationUpdates();
        }

        // Functionality for Add New Herd (Plus) Button
        FloatingActionButton fabAdd = findViewById(R.id.add_button_browse);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startNewHerd = new Intent(Browse.this, NewHerd.class);
                Browse.this.startActivityForResult(startNewHerd, NEW_HERD);
            }
        });

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case NEW_HERD: {
                if (resultCode == RESULT_OK)
                    getHerdsWithin15Miles();
            }
            break;
        }
    }

    //Get Herds within 15 miles from Firebase
    private void getHerdsWithin15Miles() {
        final List<Herd> daList = new ArrayList<>();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("herds");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                daList.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Herd h = dsp.getValue(Herd.class);
                    assert h != null;
                    Log.d("HERD", h.getTitle());
                    h.setKey(dsp.getKey());
                    Location herdLoc = new Location("dummyprovider");
                    herdLoc.setLongitude(h.getPlace().getLongitude());
                    herdLoc.setLatitude(h.getPlace().getLatitude());
                    if (herdLoc.distanceTo(myLoc) < 24140.2) { //15 miles is 24140 meters
                        daList.add(h);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadHerd:onCancelled", databaseError.toException());
            }
        });
        gotHerd = true;

        ListView herdsListView = findViewById(R.id.herds_browse);
        adapter = new ArrayAdapter<Herd>(Browse.this, R.layout.herd_list_item, R.id.herd_item_name, daList) {
            @Override
            public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
                final View view =  super.getView(position, convertView, parent);
                TextView itemName = view.findViewById(R.id.herd_item_name);
                itemName.setText(daList.get(position).getTitle());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(Browse.this,android.R.style.Theme_Material_Dialog_Alert);
                        alert.setTitle(daList.get(position).getTitle());
                        String alertText = daList.get(position).getDescription() + "\n\n" + daList.get(position).getAddress();
                        TextView itemText = new TextView(Browse.this);
                        itemText.setText(alertText);
                        itemText.setTextColor(getResources().getColor(android.R.color.white));
                        alert.setView(itemText);

                        alert.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //add user to event
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (!currentUser.getUid().equals(daList.get(position).getCreatorID())){
                                    Log.d("JOIN HERD","Add user to daList");
                                    //daList.get(position).getSubscriberID().add(currentUser.getUid());
                                    mDatabase.child(daList.get(position).getKey()).child("subscribers").child(currentUser.getUid()).setValue(true);
                                }
                                else
                                    Toast.makeText(Browse.this, "You are the herdr of this herd", Toast.LENGTH_SHORT).show();

                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                                Log.d("ALERT", "Cancel");
                            }
                        });
                        alert.show();
                    }
                });
                return view;
            }
        };
        herdsListView.setAdapter(adapter);
    }

    // Trigger new location updates at interval
    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        Log.d("LOCATION UPDATES", "START");
        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            myLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (myLoc == null)
                myLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //Log.d("MYLOCATION", myLoc.toString());
            if (!gotHerd)
                getHerdsWithin15Miles();
        }
        // Define a listener that responds to location updates
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                myLoc = location;
                if (!gotHerd)
                    getHerdsWithin15Miles();
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
