package org.misoton.goodays;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.misoton.goodays.weather.Forecast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements View.OnKeyListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private AutoCompleteTextView search_actv;
    private InputMethodManager inputMethodManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        String response = "{\"cod\":\"200\",\"message\":0.0045,\n" +
                "\"city\":{\"id\":1851632,\"name\":\"Shuzenji\",\n" +
                "\"coord\":{\"lon\":138.933334,\"lat\":34.966671},\n" +
                "\"country\":\"JP\"},\n" +
                "\"cnt\":38,\n" +
                "\"list\":[{\n" +
                "        \"dt\":1406106000,\n" +
                "        \"main\":{\n" +
                "            \"temp\":298.77,\n" +
                "            \"temp_min\":298.77,\n" +
                "            \"temp_max\":298.774,\n" +
                "            \"pressure\":1005.93,\n" +
                "            \"sea_level\":1018.18,\n" +
                "            \"grnd_level\":1005.93,\n" +
                "            \"humidity\":87},\n" +
                "        \"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\n" +
                "        \"clouds\":{\"all\":88},\n" +
                "        \"wind\":{\"speed\":5.71,\"deg\":229.501},\n" +
                "        \"sys\":{\"pod\":\"d\"},\n" +
                "        \"dt_txt\":\"2014-07-23 09:00:00\"}\n" +
                "        ]}";

        try {
            ObjectMapper mapper = new ObjectMapper();
            Forecast info = mapper.readValue(response, Forecast.class);
            Log.d("MapsActivity", "" + info.list.get(0).main.humidity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputMethodManager =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        search_actv = (AutoCompleteTextView) this.findViewById(R.id.maps_actv);
        search_actv.setOnKeyListener(this);
        updateAutoCompleteAddresses();

        setUpMapIfNeeded();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lastAddress = sharedPreferences.getString("lastAddress", "東京駅");
        updateSpot(lastAddress);

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

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

    private void setUpMap() {
        mMap.clear();
    }

    private void updateAutoCompleteAddresses(){
        List<AddressHistory> historyList = AddressHistoryManager.getHistories();

        List<String> address = new ArrayList<>();

        for(AddressHistory history: historyList){
            address.add(history.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, address);

        search_actv.setAdapter(adapter);
        search_actv.setThreshold(1);
    }

    private void updateLastAddress(String name){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastAddress", name);
        editor.apply();
    }

    private void moveMapToSpot(double lat, double lon){

        // keeping zoom value, if current value is lower than 10.0f
        float zoom = mMap.getCameraPosition().zoom;
        if(mMap.getCameraPosition().zoom < 10.0f){
            zoom = 10.0f;
        }

        CameraPosition spot = new CameraPosition.Builder()
                .target(new LatLng(lat, lon)).zoom(zoom)
                .bearing(0).tilt(0).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(spot));

    }

    private void updateMarkers(List<Address> markerAddressList){
        mMap.clear();
        for(Address ad : markerAddressList){
            this.addMarker(new LatLng(ad.getLatitude(), ad.getLongitude()));
        }
    }

    private void addMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("unk");

        mMap.addMarker(markerOptions);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch(v.getId()){
            case R.id.maps_actv:
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){

                    inputMethodManager.hideSoftInputFromWindow(search_actv.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    String input = search_actv.getText().toString();
                    updateSpot(input);
                    updateLastAddress(input);
                    return true;
                }
                break;
            default:
        }

        return false;
    }

    private void updateSpot(String address){
        if(address.equals("")){
            return;
        }

        try {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            List<Address> list = geocoder.getFromLocationName(address, 10);
            updateMarkers(list);
            moveMapToSpot(list.get(0).getLatitude(), list.get(0).getLongitude());
            addAddressHistory(address, list.get(0).getLatitude(), list.get(0).getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            Toast.makeText(this, "\"" + address + "\" is not available address.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean addAddressHistory(String name, double lat, double lon){
        boolean result =  AddressHistoryManager.addHistory(name, lat, lon);
        updateAutoCompleteAddresses();
        return result;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        addMarker(latLng);
        moveMapToSpot(latLng.latitude, latLng.longitude);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //marker.remove();
        return false;
    }
}
