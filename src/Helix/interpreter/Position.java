package Helix.interpreter;


public class Position {

    public double lat;
    public double lng;
    public double alt;

    public Position(Position pos) {
        this.lat = pos.lat;
        this.lng = pos.lng;
        this.alt = pos.alt;
    }

    public Position(double lat, double lng, double alt) {
        this.lat = lat;
        this.lng = lng;
        this.alt = alt;
    }

    public GPSPosition toGPS(GPSPosition homeLocation) {
        GPSPosition posGPS = new GPSPosition(homeLocation);
        posGPS.move(this);
        return posGPS;
    }

    public void move(Position movement) {
        lat += movement.lat;
        lng += movement.lng;
        alt += movement.alt;
    }

    public String toString(boolean inverse) {
        String s;
        if(inverse) {
            s = "" + lng + "," + lat;
        } else {
            s = "" + lat + "," + lng;
        }
        return s + "," + alt;
    }
}
