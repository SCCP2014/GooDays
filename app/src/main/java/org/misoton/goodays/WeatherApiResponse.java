package org.misoton.goodays;

import android.graphics.Bitmap;

public class WeatherApiResponse {
    private Object response;
    private boolean string_flag = false;
    private boolean bitmap_flag = false;

    WeatherApiResponse(String response){
        this.response = response;
        string_flag = true;
    }

    WeatherApiResponse(Bitmap response){
        this.response = response;
        bitmap_flag = true;
    }

    public Bitmap getBitmapResponse() {
        if(this.response instanceof Bitmap){
            return (Bitmap) this.response;
        } else {
            return null;
        }
    }

    public String getStringResponse() {
        if(this.response instanceof String){
            return (String) this.response;
        } else {
            return null;
        }
    }

    public boolean isString() {
        return string_flag;
    }

    public boolean isBitmap() {
        return bitmap_flag;
    }
}
