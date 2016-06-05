package net.aurynj.rne.locatmonster.app;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import net.aurynj.rne.locatmonster.*;
import net.aurynj.rne.locatmonster.appframework.*;
import net.aurynj.rne.locatmonster.model.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private AppCompatSpinner mSpinner;
    private GoogleMap mMap;
    private boolean mMapIsReady = false;
    private RegionClass mLastRegion;
    private Polygon mLastPolygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSpinner = (AppCompatSpinner) findViewById(R.id.activity_maps_select_region);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, RegionHelper.getRegionNames());
        mSpinner.setAdapter(arrayAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("MapsActivity", "Spinner onItemSelected: " + i);
                mLastRegion = RegionHelper.getRegion(i);
                updateMap();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.v("MapsActivity", "Spinner onNothingSelected");
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMapIsReady = true;

        // Add a marker in Sydney and move the camera
        LatLng latlngAnamStn = new LatLng(37.586296, 127.029137);
        mMap.addMarker(new MarkerOptions().position(latlngAnamStn).title("안암역"));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latlngAnamStn).zoom(16).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (mLastRegion != null) {
            updateMap();
        }
    }

    public void updateMap() {
        if (!mMapIsReady)
            return;

        if (mLastPolygon != null) {
            mLastPolygon.remove();
        }

        LatLngImpl[] regionPolygon = mLastRegion.polygon();
        LatLng[] polygon = {
                new LatLng(regionPolygon[0].latitude, regionPolygon[0].longitude),
                new LatLng(regionPolygon[1].latitude, regionPolygon[1].longitude),
                new LatLng(regionPolygon[2].latitude, regionPolygon[2].longitude),
                new LatLng(regionPolygon[3].latitude, regionPolygon[3].longitude),
        };

        PolygonOptions polygonOptions = new PolygonOptions()
                .add(polygon)
                .clickable(true)
                .strokeColor(Color.rgb(79, 146, 255))
                .strokeWidth(2.0f)
                .fillColor(Color.argb(63, 79, 146, 255));
        mLastPolygon = mMap.addPolygon(polygonOptions);
    }
}
