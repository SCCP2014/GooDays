package org.misoton.goodays;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

public class WeatherMarkerManager {
    private Marker marker;
    private String ic_code;

    public WeatherMarkerManager(Marker marker, String ic_code) {
        this.marker = marker;
        this.ic_code = ic_code;
    }

    public void testSetCuntomIcon(int resID) {
        this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.o10d));
    }

    public Marker getMarker() {
        return marker;
    }

    public String getIcCode() {
        return ic_code;
    }
}
