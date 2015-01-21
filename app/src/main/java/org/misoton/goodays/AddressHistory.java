package org.misoton.goodays;

public class AddressHistory {
    private int id;
    private String name;
    private double lat;
    private double lon;

    AddressHistory(int id, String name, double lat, double lon){
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }
    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof AddressHistory)){
            return false;
        }
        AddressHistory history = (AddressHistory)obj;
        return this.name.equals(history.getName()) && this.lat == history.getLat() && this.lon == history.getLat();
    }
}
