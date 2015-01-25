package org.misoton.goodays.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Forecast {
    public String cod;
    public double message;
    public City city;
    public int cnt;
    public List<WeatherInfo> list;
}
