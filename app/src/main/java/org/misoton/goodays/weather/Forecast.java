package org.misoton.goodays.weather;

import java.util.List;

public class Forecast {
    public String cod;
    public double message;
    public City city;
    public int cnt;
    public List<WeatherInfo> list;
}
