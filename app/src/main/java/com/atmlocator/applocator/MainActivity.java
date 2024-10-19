package com.atmlocator.applocator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.atmlocator.applocator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    Button next;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate and set the content view using binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        next = findViewById(R.id.SearchNearBy);
        String[] locationsNearBy = getResources().getStringArray(R.array.locations_nearby);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, locationsNearBy);

        // Set the adapter to the AutoCompleteTextView
        binding.autoCompleteTextView.setAdapter(arrayAdapter);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
                myIntent.putExtra("key", ""); // Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

}