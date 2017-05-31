package Helix.interpreter;

public class GPSPosition extends Position {

    private static final double EARTH_RADIUS = 6378000;


    public GPSPosition(Position pos) {
        super(pos);
    }

    public GPSPosition(double lat, double lng, double alt) {
        super(lat, lng, alt);
    }

    @Override
    public void move(Position movement) {
        double prev_lat = lat;
        lat += (movement.lat / EARTH_RADIUS) * (180 / Math.PI);
        lng += (movement.lng / EARTH_RADIUS) * (180 / Math.PI) / Math.cos(prev_lat * Math.PI/180);
        alt += movement.alt;
    }

    @Override
    public void negative_move(Position movement) {
        move(new Position(-movement.lat, -movement.lng, -movement.alt));
    }

    @Override
    public GPSPosition toGPS(GPSPosition homeLocation) {
        return this;
    }

    public Position toRelative(GPSPosition homeLocation) {
        Position relative = new Position(this);
        relative.negative_move(homeLocation);
        relative.lat *= (EARTH_RADIUS * Math.PI / 180);
        relative.lng *= (EARTH_RADIUS * Math.PI * Math.cos(homeLocation.lat * Math.PI/180) / 180);
        return relative;
    }
}
