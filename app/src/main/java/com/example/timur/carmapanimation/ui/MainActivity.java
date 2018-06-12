package com.example.timur.carmapanimation.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.timur.carmapanimation.LocationUpdateService;
import com.example.timur.carmapanimation.R;
import com.example.timur.carmapanimation.utils.PermissionUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private LatLng mStartPosition = new LatLng(42.849422, 74.592220);
    private LatLng mEndPosition = new LatLng(42.843608, 74.585977);
    private Marker mCarMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMap();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        enableMyLocation();
    }


    private void enableMyLocation() {
        if (PermissionUtils.checkLocationPermission(this) && mGoogleMap != null) {
            mGoogleMap.setMyLocationEnabled(true);
            startLocationService();
            //locationReceived();
        }
    }

    private void startLocationService(){
        startService(new Intent(this, LocationUpdateService.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void locationReceived(Location location) {
        mStartPosition = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions()
                .position(mStartPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon));
        mCarMarker = mGoogleMap.addMarker(options);
        Log.d("Location check ", "locationReceived: ");
    }
}
