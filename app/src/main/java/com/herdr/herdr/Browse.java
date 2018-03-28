package com.herdr.herdr;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krando67 on 3/6/18.
 */

public class Browse extends AppCompatActivity{
    private FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);
        Log.d("New Activity", "Browse");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Functionality for Add New Herd (Plus) Button
        FloatingActionButton fabAdd = findViewById(R.id.add_button_browse);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startNewHerds = new Intent(Browse.this, NewHerd.class);
                //browseEm.putExtra("key", value); //Optional parameters
                Browse.this.startActivity(startNewHerds);
            }
        });

        final List<Herd> theList = new ArrayList<>();
        ListView herdsListView = findViewById(R.id.herds_browse);
        ArrayAdapter adapter = new ArrayAdapter<Herd>(Browse.this, R.layout.herd_list_item, R.id.herds_browse, theList){
            @Override
            public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
                TextView itemName = findViewById(R.id.herd_item_name);

                return super.getView(position, convertView, parent);
            }
        };
        herdsListView.setAdapter(adapter);


        // Bottom Navigation
        BottomNavigationView navView = findViewById(R.id.browse_navigation);
        navView.setSelectedItemId(R.id.action_browse);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case(R.id.action_herds):
                        Intent startMyHerds = new Intent(Browse.this, MyHerds.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        Browse.this.startActivity(startMyHerds);
                        break;
                    case(R.id.action_profile):
                        Intent startProfile = new Intent(Browse.this, com.herdr.herdr.Profile.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        Browse.this.startActivity(startProfile);
                        break;
                    case(R.id.action_settings):
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
    public void onBackPressed(){
        moveTaskToBack(true);
    }

}
