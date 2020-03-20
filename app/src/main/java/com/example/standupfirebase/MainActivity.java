package com.example.standupfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.standupfirebase.model.Vehicle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView( R.id.spinnerVehicles )
    Spinner spinner;
    @BindView( R.id.vehiclesEdittext )
    EditText vehicleEdittext;
    @BindView( R.id.btnSaveVehicle )
    Button btnSave;

    private ArrayAdapter vehicleAdapter;
    private ArrayList<String> vehicleSpinnerData;

    DatabaseReference vehicleDatabaseReference;

    private ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        ButterKnife.bind( this );

        vehicleDatabaseReference = FirebaseDatabase.getInstance().getReference("Vehicle");

        vehicleSpinnerData = new ArrayList<>();
        vehicleAdapter = new ArrayAdapter<String>( MainActivity.this, R.layout.support_simple_spinner_dropdown_item, vehicleSpinnerData );


        getVehiclesFromFirebaseToSpinner();
        btnSave.setOnClickListener( this );

        spinner.setAdapter( vehicleAdapter );
    }

    private void getVehiclesFromFirebaseToSpinner() {
        valueEventListener = vehicleDatabaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    vehicleSpinnerData.add( item.getValue().toString() );
                }
                vehicleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    @Override
    public void onClick(View v) {
        if (v == btnSave) {
            saveVehicle();
        }
    }

    private void saveVehicle() {
        String vehicleName = vehicleEdittext.getText().toString().trim();

        if (!TextUtils.isEmpty( vehicleName )) {
            String vehicle_name = vehicleDatabaseReference.push().getKey();
            Vehicle vehicle = new Vehicle( vehicleName );
            assert vehicle_name != null;
            vehicleDatabaseReference.child( vehicle_name ).setValue( vehicle );
            Toast.makeText( MainActivity.this, "New Vehicle added successfully", Toast.LENGTH_LONG ).show();
            vehicleEdittext.setText( "" );
        } else {
            Toast.makeText( MainActivity.this, "Please provide vehicle name", Toast.LENGTH_LONG ).show();
        }
    }
}
