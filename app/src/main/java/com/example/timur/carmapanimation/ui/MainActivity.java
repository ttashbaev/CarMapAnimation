package com.example.timur.carmapanimation.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.example.timur.carmapanimation.LocationUpdateService;
import com.example.timur.carmapanimation.R;
import com.example.timur.carmapanimation.utils.AndroidUtils;
import com.example.timur.carmapanimation.utils.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private LatLng mStartPosition = new LatLng(42.833415, 74.621878);
    private LatLng mEndPosition = new LatLng(42.843608, 74.585977);
    private Marker mCarMarker;

    private ArrayList<LatLng> mRouteList = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationProviderClient;

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
        //getFirstLocation();
    }


    private void enableMyLocation() {
        if (PermissionUtils.checkLocationPermission(this) && mGoogleMap != null) {
            mGoogleMap.setMyLocationEnabled(true);
            getRouteList();
        }
    }

    private void startLocationService(){
        //startService(new Intent(this, LocationUpdateService.class));
        getRouteList();
    }

    private void getRouteList() {
        GoogleDirection.withServerKey(getString(R.string.google_api))
                .from(mStartPosition)
                .to(mEndPosition)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            mRouteList = direction
                                    .getRouteList()
                                    .get(0)
                                    .getLegList()
                                    .get(0)
                                    .getDirectionPoint();

                            drawRoute(mRouteList);
                        } else {
                            AndroidUtils.showShortToast(MainActivity.this, "Error in direction");
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        AndroidUtils.showShortToast(MainActivity.this, t.getMessage());
                    }
                });
    }

    private void drawRoute(ArrayList<LatLng> routeList) {
        PolylineOptions options = DirectionConverter
                .createPolyline(
                        this, routeList, 5, Color.parseColor("#4A90E2"));
        mGoogleMap.addPolyline(options);
        MarkerOptions carMarker = new MarkerOptions().
                position(mStartPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon));
        mGoogleMap.addMarker(carMarker);

        MarkerOptions targetMarket = new MarkerOptions().
                position(mEndPosition)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mGoogleMap.addMarker(targetMarket);
        /*for (int i = 0; i < 2; i++) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(i == 0 ? mStartPosition : mEndPosition)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon));

            mGoogleMap.addMarker(markerOptions);

        }*/
    }


}
