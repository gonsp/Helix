package Helix.interpreter;


public class Position {

    public double lat;
    public double lng;
    public double alt;
    
    public Position(double lat, double lng, double alt) {
        this.lat = lat;
        this.lng = lng;
        this.alt = alt;
    }
    
    public String toString() {
        return "" + lat + "," + lng + "," + alt;
    }
}
