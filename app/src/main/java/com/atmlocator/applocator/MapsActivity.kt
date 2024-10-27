package com.atmlocator.applocator

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.atmlocator.applocator.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import java.util.Arrays


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize Places API and client
        val apiKey = "AIzaSyDBsvEHnIdSizq5vEsCs6YcCSA3TUiSOfs"
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        placesClient = Places.createClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a default marker in Sydney and move the camera there initially
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))

        // Start searching for nearby places
        searchNearBy(sydney)
    }

    private fun searchNearBy(location: LatLng) {
        // Define the place fields to retrieve
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        // Set up a circular search area
        val circle = CircularBounds.newInstance(location, 10000.0) // Radius in meters
        val includedTypes = listOf("restaurant", "cafe")
        val excludedTypes = listOf("pizza_restaurant", "american_restaurant")

        // Create a SearchNearbyRequest
        val searchNearbyRequest = SearchNearbyRequest.builder(circle, placeFields)
            .setIncludedTypes(includedTypes)
            .setExcludedTypes(excludedTypes)
            .setMaxResultCount(10)
            .build()

        // Execute the search
        placesClient.searchNearby(searchNearbyRequest)
            .addOnSuccessListener { response ->
                // Loop through each place in the response and add a marker on the map
                for (place in response.places) {
                    val placeLatLng = place.latLng
                    if (placeLatLng != null) {
                        mMap.addMarker(
                            MarkerOptions()
                                .position(placeLatLng)
                                .title(place.name)
                        )
                    }
                }
                // Adjust the camera to fit all markers (optional)
                if (response.places.isNotEmpty()) {
                    val firstPlace = response.places[0].latLng
                    firstPlace?.let {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 14f))
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MapsActivity", "Place search failed: ${exception.message}")
            }
    }
}