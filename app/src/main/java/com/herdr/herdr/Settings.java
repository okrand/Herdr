package com.herdr.herdr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Krando67 on 3/10/18.
 */

public class Settings extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Log.d("New Activity", "Settings");
        mAuth = FirebaseAuth.getInstance();


        Button logOutB = findViewById(R.id.logout_button);
        logOutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LOGOUT", com.facebook.Profile.getCurrentProfile().getFirstName());
                final Intent cleanSlate = new Intent(Settings.this, Login.class);
                Settings.this.startActivity(cleanSlate);
                LoginManager.getInstance().logOut();
                mAuth.signOut();
                finish();
            }
        });

        BottomNavigationView navView = findViewById(R.id.settings_navigation);
            navView.setSelectedItemId(R.id.action_settings);
            navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case(R.id.action_browse):
                        Intent startBrowse = new Intent(Settings.this, Browse.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        Settings.this.startActivity(startBrowse);
                        break;
                    case(R.id.action_herds):
                        Intent startMyHerds = new Intent(Settings.this, com.herdr.herdr.MyHerds.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        Settings.this.startActivity(startMyHerds);
                        break;
                    case(R.id.action_profile):
                        Intent startProfile = new Intent(Settings.this, com.herdr.herdr.Profile.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        Settings.this.startActivity(startProfile);
                        break;
                }
                return false;
            }
        });
    }
}
