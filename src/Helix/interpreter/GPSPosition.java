package Helix.interpreter;

public class GPSPosition extends Position {

    private static final double EARTH_RADIOUS = 6378000;


    public GPSPosition(Position pos) {
        super(pos);
    }

    public GPSPosition(double lat, double lng, double alt) {
        super(lat, lng, alt);
    }

    @Override
    public void move(Position movement) {
        lat += (movement.lat / EARTH_RADIOUS) * (180 / Math.PI);
        lng += (movement.lng / EARTH_RADIOUS) * (180 / Math.PI) / Math.cos(lat * Math.PI/180);
        alt += movement.alt;
    }

    @Override
    public GPSPosition toGPS(GPSPosition homeLocation) {
        return this;
    }

    public Position toRelative(GPSPosition homeLocation) {
        // TODO implement this (maybe not necessary)
        return null;
    }
}
