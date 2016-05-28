package net.aurynj.rne.locatmonster.app;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.aurynj.rne.locatmonster.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        LatLng latlngAnamStn = new LatLng(37.586296, 127.029137);
        mMap.addMarker(new MarkerOptions().position(latlngAnamStn).title("안암역"));

        final int gridPtSpan = 2;
        final int gridSidePts = 2 * gridPtSpan + 1, gridAllPts = gridSidePts * gridSidePts;
        LatLng latlngGridCenter = new LatLng(37.586, 127.028);
        double gridLen = 0.002;
        LatLng[] latlngGridPoints = new LatLng[gridAllPts];
        for (int i = 0, x = -gridPtSpan; x <= gridPtSpan; i++, x++) {
            for (int j = 0, y = -gridPtSpan; y <= gridPtSpan; j++, y++) {
                final int idx = i * gridPtSpan + j;
                latlngGridPoints[idx] = new LatLng(latlngGridCenter.latitude + x * gridLen, latlngGridCenter.longitude + y * gridLen);
                mMap.addMarker(new MarkerOptions().position(latlngGridPoints[idx]).title("Grid " + x + ", " + y));
            }
        }

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latlngAnamStn).zoom(16).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
