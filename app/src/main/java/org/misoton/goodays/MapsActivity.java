package org.misoton.goodays;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.misoton.goodays.weather.Forecast;
import org.misoton.goodays.weather.Weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<WeatherApiResponse>, View.OnKeyListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private AutoCompleteTextView search_actv;
    private ImageView test_iv;
    private InputMethodManager inputMethodManager;
    private SharedPreferences sharedPreferences;
    private int loaderId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        search_actv = (AutoCompleteTextView) this.findViewById(R.id.maps_actv);
        search_actv.setOnKeyListener(this);
        updateAutoCompleteAddresses();

        setUpMapIfNeeded();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lastAddress = sharedPreferences.getString("lastAddress", "東京駅");
        updateSpot(lastAddress);

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);

        test_iv = (ImageView) this.findViewById(R.id.test_iv);
        test_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
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

    private void updateAutoCompleteAddresses() {
        List<AddressHistory> historyList = AddressHistoryManager.getHistories();

        List<String> address = new ArrayList<>();

        for (AddressHistory history : historyList) {
            address.add(history.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, address);

        search_actv.setAdapter(adapter);
        search_actv.setThreshold(1);
    }

    private void updateLastAddress(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastAddress", name);
        editor.apply();
    }

    private void moveMapToSpot(double lat, double lon) {

        // keeping zoom value, if current value is lower than 10.0f
        float zoom = mMap.getCameraPosition().zoom;
        if (mMap.getCameraPosition().zoom < 10.0f) {
            zoom = 10.0f;
        }

        CameraPosition spot = new CameraPosition.Builder()
                .target(new LatLng(lat, lon)).zoom(zoom)
                .bearing(0).tilt(0).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(spot));

    }

    private void updateMarkers(List<Address> markerAddressList) {
        mMap.clear();
        for (Address ad : markerAddressList) {
            this.callLoaderForecast(ad);
        }
    }

    private void callLoaderForecast(Address address){
        Bundle bundle = new Bundle();
        bundle.putBoolean("forecast", true);
        bundle.putDouble("lat", address.getLatitude());
        bundle.putDouble("lon", address.getLongitude());
        this.getSupportLoaderManager().initLoader(loaderId++, bundle, this);
    }

    private void callLoaderForecast(LatLng latLng){
        Bundle bundle = new Bundle();
        bundle.putBoolean("forecast", true);
        bundle.putDouble("lat", latLng.latitude);
        bundle.putDouble("lon", latLng.longitude);
        this.getSupportLoaderManager().initLoader(loaderId++, bundle, this);
    }

    private void addMarker(LatLng latLng, BitmapDescriptor descriptor) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(descriptor);
        markerOptions.visible(true);
        mMap.addMarker(markerOptions);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (v.getId()) {
            case R.id.maps_actv:
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

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

    private void updateSpot(String address) {
        if (address.equals("")) {
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
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            Toast.makeText(this, "\"" + address + "\" is not available address.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean addAddressHistory(String name, double lat, double lon) {
        boolean result = AddressHistoryManager.addHistory(name, lat, lon);
        updateAutoCompleteAddresses();
        return result;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        this.callLoaderForecast(latLng);
        moveMapToSpot(latLng.latitude, latLng.longitude);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //marker.remove();
        return false;
    }

    @Override
    public Loader<WeatherApiResponse> onCreateLoader(int id, Bundle args) {
        WeatherApiLoader loader;
        if(args.getBoolean("forecast", true)) {
            loader = new WeatherApiLoader(getApplication(), WeatherApiLoader.MODE_FORECAST);
            loader.setLocation(args.getDouble("lat"), args.getDouble("lon"));
        } else {
            loader = new WeatherApiLoader(getApplication(), WeatherApiLoader.MODE_GETIMAGE);
            loader.setImageName(args.getString("icon"));
        }
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<WeatherApiResponse> loader, WeatherApiResponse data) {


        if(data.isString()){
            Log.d("Map", data.getStringResponse());
            try {
                ObjectMapper mapper = new ObjectMapper();
                Forecast forecast = mapper.readValue(data.getStringResponse(), Forecast.class);
                Toast.makeText(this, forecast.list.get(0).weather.get(0).main, Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                bundle.putBoolean("forecast", false);
                bundle.putString("icon", forecast.list.get(0).weather.get(0).icon);
                this.getSupportLoaderManager().initLoader(loaderId++, bundle, this);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if(data.isBitmap()) {
            Toast.makeText(this, "error1", Toast.LENGTH_LONG).show();
            Log.d("Map", "response is BitMap");
            WeatherApiLoader weatherApiLoader = (WeatherApiLoader)loader;
            test_iv.setImageBitmap(data.getBitmapResponse());
            this.addMarker(new LatLng(weatherApiLoader.getLat(), weatherApiLoader.getLon()),
                    BitmapDescriptorFactory.fromBitmap(data.getBitmapResponse()));

        } else {
            Toast.makeText(this, "error2", Toast.LENGTH_LONG).show();
            Log.d("Map", "unknown response");
        }
    }

    @Override
    public void onLoaderReset(Loader<WeatherApiResponse> loader) {
    }
}
