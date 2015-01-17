package org.misoton.goodays.weather;

import java.util.List;

public class WeatherInfo {
    public int dt;
    public String dt_txt;
    public MainInfo main;
    public List<Weather> weather;
    public Clouds clouds;
    public Wind wind;
    public Rain rain;
    public Snow snow;
    public Sys sys;
}
