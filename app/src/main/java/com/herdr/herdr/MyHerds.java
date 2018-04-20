package com.herdr.herdr;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krando67 on 3/8/18.
 */

public class MyHerds extends AppCompatActivity {
    String TAG = "MY HERDS";
    private static final int NEW_HERD = 9;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_herds);

        // Functionality for Add New Herd (Plus) Button
        FloatingActionButton fabAdd = findViewById(R.id.add_button_my_herds);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startNewHerds = new Intent(MyHerds.this, NewHerd.class);
                //browseEm.putExtra("key", value); //Optional parameters
                MyHerds.this.startActivityForResult(startNewHerds, NEW_HERD);
            }
        });

        populateLeadingHerds();
        populateParticipatingHerds();

        //Navigation View
        BottomNavigationView navView = findViewById(R.id.myherds_navigation);
        navView.setSelectedItemId(R.id.action_herds);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case(R.id.action_browse):
                        Intent startBrowse = new Intent(MyHerds.this, Browse.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        MyHerds.this.startActivity(startBrowse);
                        break;
                    case(R.id.action_profile):
                        Intent startProfile = new Intent(MyHerds.this, com.herdr.herdr.Profile.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        MyHerds.this.startActivity(startProfile);
                        break;
                    case(R.id.action_settings):
                        Intent startSettings = new Intent(MyHerds.this, Settings.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        MyHerds.this.startActivity(startSettings);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case NEW_HERD: {
                if (resultCode == RESULT_OK) {
                    populateLeadingHerds();
                    populateParticipatingHerds();
                }
            }
            break;
        }
    }

    //Get Herds user is leading from Firebase
    private void populateLeadingHerds() {
        final List<Herd> daList = new ArrayList<>();
        Query queryRef = FirebaseDatabase.getInstance().getReference().child("herds").orderByChild("creatorID").equalTo(currentUser.getUid());
        //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("herds");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Herd h = dsp.getValue(Herd.class);
                    assert h != null;
                    Log.d("HERD", h.getTitle());
                    daList.add(h);
                }
                ListView leadingListView = findViewById(R.id.list_leading);
                ArrayAdapter adapter = new ArrayAdapter<Herd>(MyHerds.this, R.layout.herd_list_item, R.id.herd_item_name, daList) {
                    @Override
                    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
                        final View view =  super.getView(position, convertView, parent);
                        TextView itemName = view.findViewById(R.id.herd_item_name);
                        itemName.setText(daList.get(position).getTitle());
                        return view;
                    }
                };
                leadingListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadLeadingHerd:onCancelled", databaseError.toException());
            }
        });
    }

    private void populateParticipatingHerds() {
        final List<Herd> daList = new ArrayList<>();
        //Query queryRef = FirebaseDatabase.getInstance().getReference().child("herds"));
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("herds");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Herd h = dsp.getValue(Herd.class);
                    assert h != null;
                    List<String> subs = h.getSubscriberID();
                    if (subs.contains(currentUser.getUid())) {
                        Log.d("HERD", h.getTitle());
                        daList.add(h);
                    }
                }
                ListView leadingListView = findViewById(R.id.list_part_of);
                ArrayAdapter adapter = new ArrayAdapter<Herd>(MyHerds.this, R.layout.herd_list_item, R.id.herd_item_name, daList) {
                    @Override
                    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
                        final View view =  super.getView(position, convertView, parent);
                        TextView itemName = view.findViewById(R.id.herd_item_name);
                        itemName.setText(daList.get(position).getTitle());
                        return view;
                    }
                };
                leadingListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadLeadingHerd:onCancelled", databaseError.toException());
            }
        });
    }

}
