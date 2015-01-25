package org.misoton.goodays;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

public class WeatherApiLoader extends AsyncTaskLoader<WeatherApiResponse> {
    public static final int MODE_FORECAST = 0;
    public static final int MODE_GETIMAGE = 1;

    private int mode;
    private double lat;
    private double lon;
    private String imageName;

    public WeatherApiLoader(Context context, int mode) {
        super(context);
        this.mode = mode;
    }

    public void setLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public void setImageName(String name){
        this.imageName = name;
    }

    @Override
    public WeatherApiResponse loadInBackground() {
        String url = "";
        switch (this.mode){
            case WeatherApiLoader.MODE_FORECAST:
                url = "http://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&mode=json";
                break;

            case WeatherApiLoader.MODE_GETIMAGE:
                url = "http://openweathermap.org/img/w/" + imageName + ".png";
                break;
            default:
        }

        HttpClient httpClient = new DefaultHttpClient();

        try {
            WeatherApiResponse responseBody = httpClient.execute(new HttpGet(url),

                    new ResponseHandler<WeatherApiResponse>() {

                        @Override
                        public WeatherApiResponse handleResponse(HttpResponse response)
                                throws ClientProtocolException, IOException {

                            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                                switch (WeatherApiLoader.this.mode){
                                    case WeatherApiLoader.MODE_FORECAST:
                                        return new WeatherApiResponse(EntityUtils.toString(response.getEntity(), "UTF-8"));

                                    case WeatherApiLoader.MODE_GETIMAGE:
                                        return new WeatherApiResponse(BitmapFactory.decodeStream(response.getEntity().getContent()));

                                    default:
                                }
                            }
                            return null;
                        }
                    });

            return responseBody;
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return null;
    }


    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getMode() {
        return mode;
    }

    public String getImageName() {
        return imageName;
    }
}
