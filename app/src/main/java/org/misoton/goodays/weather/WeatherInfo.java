package org.misoton.goodays.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherInfo {
    public long dt;
    public String dt_txt;
    public MainInfo main;
    public List<Weather> weather;
    public Clouds clouds;
    public Wind wind;
    public Rain rain;
    public Snow snow;
    public Sys sys;
}
