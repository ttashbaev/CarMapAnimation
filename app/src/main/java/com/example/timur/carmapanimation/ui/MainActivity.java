package com.example.timur.carmapanimation.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private LinearLayout mLlBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private Button btnStart, btnStop;
    EditText mEtGetLocat;
    private GoogleMap mGoogleMap;
    private float fraction, prevRotate;
    private double lat, lon;
    private LatLng mStartPosition = new LatLng(42.8746392, 74.5904465);
    private LatLng mEndPosition = new LatLng(42.000, 74.585977);
    private Marker mCarMarker;
    private Polyline myPolyline, secondPolyline;
    private List<LatLng> polylineList;
    int index, next;

    private ArrayList<LatLng> mRouteList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEtGetLocat = findViewById(R.id.etGetLocat);

        mLlBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mLlBottomSheet);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        initMap();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                Toast.makeText(this, "sdasdasdsadasd", Toast.LENGTH_SHORT).show();
                //
                break;
            case R.id.btnStop:
                Toast.makeText(this, "sdasdasdsadasd232232323", Toast.LENGTH_SHORT).show();
                //
                break;
        }

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
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mStartPosition));
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(googleMap.getCameraPosition().target)
                .zoom(17)
                .bearing(30)
                .tilt(45)
                .build()));

        //getFirstLocation();
    }


    private void enableMyLocation() {
        if (PermissionUtils.checkLocationPermission(this) && mGoogleMap != null) {
            mGoogleMap.setMyLocationEnabled(true);
            getRouteList();
        }
    }

    private void getRouteList() {
        GoogleDirection.withServerKey(getString(R.string.google_api))
                .from(mStartPosition)

                .to(mEndPosition)
                .transportMode(TransportMode.DRIVING)
                .optimizeWaypoints(true)
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
        myPolyline = mGoogleMap.addPolyline(options);
        PolylineOptions blackPolyline = DirectionConverter
                .createPolyline(
                        this, routeList, 5, Color.parseColor("#4A90E8"));
        secondPolyline = mGoogleMap.addPolyline(blackPolyline);


        MarkerOptions targetMarket = new MarkerOptions().
                position(mEndPosition)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mGoogleMap.addMarker(targetMarket);
        drawCarMarker();
        animateCar(routeList);
        /*for (int i = 0; i < 2; i++) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(i == 0 ? mStartPosition : mEndPosition)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon));


            mGoogleMap.addMarker(markerOptions);

        }*/
    }

    private void drawCarMarker() {


    }

    private void animateCar(ArrayList<LatLng> routeList) {
        final ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
        polylineAnimator.setDuration(2000);
        polylineAnimator.setInterpolator(new LinearInterpolator());
        polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                List<LatLng> points = myPolyline.getPoints();
                int percentValue = (int) polylineAnimator.getAnimatedValue();
                int size = points.size();
                int newPoints = (int) (size * (percentValue / 100.0f));
                List<LatLng> p = points.subList(0, newPoints);
                secondPolyline.setPoints(p);
            }
        });
        polylineAnimator.start();
        MarkerOptions options = new MarkerOptions().position(mStartPosition).title("Bishkek")
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon));
        mCarMarker = mGoogleMap.addMarker(options);

        // car moving

        final Handler handler = new Handler();
        index = -1;
        next = 1;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < myPolyline.getPoints().size() - 1) {
                    index++;
                    next = index + 1;
                }

                if (index < myPolyline.getPoints().size() - 1) {
                    mStartPosition = mRouteList.get(index);
                    mEndPosition = mRouteList.get(next);
                }
                final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(3000);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        fraction = valueAnimator.getAnimatedFraction();
                        lon = fraction * mEndPosition.longitude + (1 - fraction)
                                * mStartPosition.longitude;
                        lat = fraction * mEndPosition.latitude + (1 - fraction)
                                * mStartPosition.latitude;


                        LatLng newPos = new LatLng(new BigDecimal(lat).
                                setScale(6, RoundingMode.CEILING).doubleValue()
                                , new BigDecimal(lon).
                                setScale(6, RoundingMode.CEILING).doubleValue());
//
//                        Log.d("lat/lon", "onAnimationUpdate: " + new BigDecimal(lat).
//                                setScale(6, RoundingMode.CEILING).doubleValue()
//                                + " " + new BigDecimal(lon).
//                                setScale(6, RoundingMode.CEILING).doubleValue());

                        mCarMarker.setPosition(newPos);
                        mCarMarker.setAnchor(0.5f, 0.5f);

                        Location startLocation = new Location(LocationManager.GPS_PROVIDER);
                        startLocation.setLatitude(mStartPosition.latitude);
                        startLocation.setLongitude(mStartPosition.longitude);

                        Location endLocation = new Location(LocationManager.GPS_PROVIDER);
                        endLocation.setLatitude(mEndPosition.latitude);
                        endLocation.setLongitude(mEndPosition.longitude);
//                        Log.d("location", "onAnimationUpdate: " + startLocation.getLatitude()
//                         + " " + startLocation.getLongitude());
                        float hearing = startLocation.bearingTo(endLocation);
                        hearing = Math.round(hearing - 230);

                        if (hearing != prevRotate) {

                            mCarMarker.setRotation(Math.round(hearing));
                            Log.d("runnable", "onAnimationUpdate: " + hearing);
                        }
                        prevRotate = hearing;

                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newCameraPosition
                                        (new CameraPosition.Builder()
                                                .target(newPos)
                                                .zoom(15.5f)
                                                .build()));
                    }
                });
                valueAnimator.start();
                handler.postDelayed(this, 3000);

            }
        }, 3000);


    }

    private float normalizeDegree(float value) {
        if (value >= 0.0f && value <= 180.0f) {
            return value;
        } else {
            return 180 + (180 + value);
        }
    }

    private float getBearing(LatLng startPosition, LatLng newPos) {

        double PI = 3.14159;
        double lat1 = startPosition.latitude * PI / 180;
        double long1 = startPosition.longitude * PI / 180;
        double lat2 = newPos.latitude * PI / 180;
        double long2 = newPos.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;


        return (float) brng;

    }
}
