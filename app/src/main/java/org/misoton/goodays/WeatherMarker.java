package org.misoton.goodays;

import com.google.android.gms.maps.model.Marker;

import org.misoton.goodays.weather.Forecast;

public class WeatherMarker {
    public Marker marker;
    public Forecast forecast;

    WeatherMarker(Marker marker, Forecast forecast){
        this.marker = marker;
        this.forecast = forecast;
    }

    public void onClick(){

    }
}
