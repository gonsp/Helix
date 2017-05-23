package Helix.interpreter;

public class GPSPosition extends Position {

    public GPSPosition(Position pos) {
        super(pos);
    }

    public GPSPosition(double lat, double lng, double alt) {
        super(lat, lng, alt);
    }

    @Override
    public void move(Position movement) {
        // TODO implement this
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
