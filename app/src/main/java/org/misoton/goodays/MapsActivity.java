package org.misoton.goodays;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements View.OnKeyListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private AutoCompleteTextView search_actv;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        inputMethodManager =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        search_actv = (AutoCompleteTextView) this.findViewById(R.id.maps_actv);

        String[] address = new String[] {"会津若松"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, address);

        search_actv.setAdapter(adapter);
        search_actv.setThreshold(1);
        search_actv.setOnKeyListener(this);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setLogo(R.drawable.clear_outing);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background_res));

        setUpMapIfNeeded();

        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> list = geocoder.getFromLocationName("東京駅", 10);
            Toast.makeText(this, "" + list.get(0).getLatitude() + list.get(0).getLongitude(), Toast.LENGTH_LONG).show();
            CameraPosition tokyo = new CameraPosition.Builder()
                    .target(new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude())).zoom(12.0f)
                    .bearing(0).tilt(0).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(tokyo));

            for(Address ad : list){
                mMap.addMarker(new MarkerOptions().position(new LatLng(ad.getLatitude(), ad.getLongitude())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.clear();
    }

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String searchWord) {
            // SubmitボタンorEnterKeyを押されたら呼び出されるメソッド
            try {
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                List<Address> list = geocoder.getFromLocationName(searchWord, 10);
                Toast.makeText(MapsActivity.this, "" + list.get(0).getLatitude() + list.get(0).getLongitude(), Toast.LENGTH_LONG).show();
                updateMarkers(list);
                moveMapToSpot(list.get(0).getLatitude(), list.get(0).getLongitude());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            // 入力される度に呼び出される
            return false;
        }
    };

    private void moveMapToSpot(double lat, double lon){
        CameraPosition spot = new CameraPosition.Builder()
                .target(new LatLng(lat, lon)).zoom(12.0f)
                .bearing(0).tilt(0).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(spot));
    }

    private void updateMarkers(List<Address> markerAddressList){
        mMap.clear();
        for(Address ad : markerAddressList){
            mMap.addMarker(new MarkerOptions().position(new LatLng(ad.getLatitude(), ad.getLongitude())));
        }
    }

    private void addMarker(LatLng latLng){
        mMap.addMarker(new MarkerOptions().position(latLng));
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch(v.getId()){
            case R.id.maps_actv:
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    //キーボードを閉じる
                    inputMethodManager.hideSoftInputFromWindow(search_actv.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    updateSpot(search_actv.getText().toString());
                    return true;
                }
                break;
            default:
        }

        return false;
    }

    private void updateSpot(String address){
        try {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            List<Address> list = geocoder.getFromLocationName(address, 10);
            Toast.makeText(MapsActivity.this, "" + list.get(0).getLatitude() + list.get(0).getLongitude(), Toast.LENGTH_LONG).show();
            updateMarkers(list);
            moveMapToSpot(list.get(0).getLatitude(), list.get(0).getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        addMarker(latLng);
        moveMapToSpot(latLng.latitude, latLng.longitude);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.remove();
        return false;
    }
}
