package org.misoton.goodays;

import com.google.android.gms.maps.model.Marker;

import org.misoton.goodays.weather.Forecast;

import java.util.ArrayList;
import java.util.List;

public class MarkerManager {
    private List<WeatherMarker> markers;

    public MarkerManager(){
        this.markers = new ArrayList<>();
    }

    public void removeAllMarkers(){
        for(WeatherMarker marker: markers){
            marker.marker.remove();
        }
        markers.clear();
    }

    public void addMarker(WeatherMarker marker){
        markers.add(marker);
    }

    public List<WeatherMarker> getMarkers(){
        return markers;
    }

    public Forecast getMarkerForecast(Marker marker){
        for(WeatherMarker weatherMarker: markers){
            if(weatherMarker.marker.equals(marker)){
                return weatherMarker.forecast;
            }
        }
        return null;
    }
}
