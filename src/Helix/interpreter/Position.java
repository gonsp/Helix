package Helix.interpreter;


public class Position {

    public float lat;
    public float lng;
    public float alt;
    
    public Position(float lat, float lng, float alt) {
        this.lat = lat;
        this.lng = lng;
        this.alt = alt;
    }
    
    public String toString() {
        return "" + lat + ", " + lng + ", " + alt;
    }
}
