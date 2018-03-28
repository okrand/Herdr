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

/**
 * Created by Krando67 on 3/8/18.
 */

public class MyHerds extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_herds);
        Log.d("New Activity", "MyHerds");

        // Functionality for Add New Herd (Plus) Button
        FloatingActionButton fabAdd = findViewById(R.id.add_button_my_herds);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startNewHerds = new Intent(MyHerds.this, NewHerd.class);
                //browseEm.putExtra("key", value); //Optional parameters
                MyHerds.this.startActivity(startNewHerds);
            }
        });

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

}
