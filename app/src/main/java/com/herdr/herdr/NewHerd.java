package com.herdr.herdr;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by Krando67 on 3/10/18.
 */

public class NewHerd extends AppCompatActivity {

    int hour;
    int minute;
    int AMPM; //0 = AM, 1 = PM
    LatLon myPlace;
    String address;
    private FirebaseAuth mAuth;
    final int PLACE_PICKER_REQUEST = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_herd);
        Log.d("New Activity", "New Herd");
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        final DatabaseReference pushRef = FirebaseDatabase.getInstance().getReference("herds").push();
        final Herd nHerd = new Herd();
        Button button_create = findViewById(R.id.button_create_herd);
        final EditText titl = findViewById(R.id.title_new_herd);
        final EditText desc = findViewById(R.id.description_new_herd);
        final RadioButton today = findViewById(R.id.today_radio);

        //Time Picker
        final EditText eventTime = findViewById(R.id.event_time_edittext);
        final Calendar mcurrentTime = Calendar.getInstance();
        int initialHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int initialMinute = mcurrentTime.get(Calendar.MINUTE);
        hour = initialHour;
        minute = initialMinute;
        AMPM = 0;

        String initialAMPM = "AM";
        if (initialHour > 12){
            AMPM = 1;
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
                        AMPM = 0;
                        if (selectedHour > 12){
                            AMPM = 1;
                            selectedAMPM = "PM";
                            selectedHour -= 12;
                        }
                        String strMin = String.valueOf(selectedMinute);
                        String strHour = String.valueOf(selectedHour);
                        if (selectedHour == 0)
                            strHour = "0" + strHour;
                        if (selectedMinute < 10){
                            strMin = "0" + strMin;
                        }
                        eventTime.setText( strHour + ":" + strMin + " " + selectedAMPM);
                    }
                }, hour, minute, false); //True = 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        //Location Picker
        final EditText pickAPlace = findViewById(R.id.selected_place);
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

        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar todate = Calendar.getInstance();
                todate.set(Calendar.HOUR_OF_DAY, hour);
                todate.set(Calendar.MINUTE, minute);

                if (String.valueOf(pickAPlace.getText()).equals("") || pickAPlace.getText() == null) {
                    Toast.makeText(getApplicationContext(),"All herds must have a location", Toast.LENGTH_LONG).show();
                }
                else if (String.valueOf(titl.getText()) == null || String.valueOf(titl.getText()).equals(""))
                    Toast.makeText(getApplicationContext(),"All herds must have a title", Toast.LENGTH_LONG).show();
                else if (String.valueOf(desc.getText()) == null || String.valueOf(desc.getText()).equals(""))
                    Toast.makeText(getApplicationContext(),"All herds must have a description", Toast.LENGTH_LONG).show();
                else{
                    nHerd.setTitle(String.valueOf(titl.getText()));
                    nHerd.setDescription(String.valueOf(desc.getText()));
                    nHerd.setCreatorID(currentUser.getUid());
                    nHerd.setPlace(myPlace);
                    nHerd.setAddress(address);
                    if (!today.isChecked())
                        todate.add(Calendar.DATE, 1);
                    nHerd.setCal(todate.getTime());
                    nHerd.setTime(String.valueOf(eventTime.getText()));
                    pushRef.setValue(nHerd);
                    setResult(RESULT_OK);
                    finish();
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
                myPlace = new LatLon(place.getLatLng().latitude, place.getLatLng().longitude);
                address = place.getAddress().toString();
            }
        }
    }
}

