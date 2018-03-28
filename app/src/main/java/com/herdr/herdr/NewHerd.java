package com.herdr.herdr;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

/**
 * Created by Krando67 on 3/10/18.
 */

public class NewHerd extends AppCompatActivity {

    int hour;
    int minute;
    private FirebaseAuth mAuth;
    final int PLACE_PICKER_REQUEST = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_herd);
        Log.d("New Activity", "New Herd");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Time Picker
        final EditText eventTime = findViewById(R.id.event_time_edittext);
        final Calendar mcurrentTime = Calendar.getInstance();
        int initialHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int initialMinute = mcurrentTime.get(Calendar.MINUTE);
        hour = initialHour;
        minute = initialMinute;

        String initialAMPM = "AM";
        if (initialHour > 12){
            initialAMPM = "PM";
            initialHour -= 12;
        }
        String initialTime = ("" + initialHour + ":" + initialMinute + " " + initialAMPM);
        eventTime.setText(initialTime);

        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                String lastTime = eventTime.getText().toString();
                Log.d("LASTTIME", lastTime);

                mTimePicker = new TimePickerDialog(NewHerd.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour = selectedHour;
                        minute = selectedMinute;
                        String selectedAMPM = "AM";
                        if (selectedHour > 12){
                            selectedAMPM = "PM";
                            selectedHour -= 12;
                        }
                        eventTime.setText( selectedHour + ":" + selectedMinute + " " + selectedAMPM);
                    }
                }, hour, minute, false); //True = 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        //Location Picker
        EditText pickAPlace = findViewById(R.id.selected_place);
        pickAPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder ppBuilder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(ppBuilder.build(NewHerd.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                EditText placeNameBox = findViewById(R.id.selected_place);
                placeNameBox.setText(place.getName());

                Toast.makeText(this, place.getName(), Toast.LENGTH_LONG).show();
            }
        }
    }
}

