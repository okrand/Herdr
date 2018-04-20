package com.herdr.herdr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Krando67 on 3/8/18.
 */

public class Profile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Log.d("New Activity", "Profile");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        com.facebook.Profile facebookProfile = com.facebook.Profile.getCurrentProfile();
        //get profile picture
        ProfilePictureView profilePictureView = findViewById(R.id.profile_picture);
        profilePictureView.setProfileId(facebookProfile.getId());

        //get profile name
        TextView userName = findViewById(R.id.profile_name);
        userName.setText(facebookProfile.getFirstName());

        // Bottom Navigation
        BottomNavigationView navView = findViewById(R.id.profile_navigation);
        navView.setSelectedItemId(R.id.action_profile);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case(R.id.action_browse):
                        Intent startBrowse = new Intent(Profile.this, Browse.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        Profile.this.startActivity(startBrowse);
                        break;
                    case (R.id.action_herds):
                        Intent startMyHerds = new Intent(Profile.this, MyHerds.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        Profile.this.startActivity(startMyHerds);
                        break;
                    case (R.id.action_settings):
                        Intent startSettings = new Intent(Profile.this, Settings.class);
                        //browseEm.putExtra("key", value); //Optional parameters
                        Profile.this.startActivity(startSettings);
                        break;
                }
                return false;
            }
        });
    }
}

