package Helix.interpreter;

import Helix.parser.HelixLexer;

public class Position extends Data {
    private static double epsilon = 0.005;

    public double lat;
    public double lng;
    public double alt;


    public Position() {
        this(0.0, 0.0, 0.0);
    }


    public Position(Position pos) {
        this(pos.lat, pos.lng, pos.alt);
    }


    public Position(double lat, double lng, double alt) {
        this.type = DataType.POSITION;
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

    public void negative_move(Position movement) {
        move(new Position(-movement.lat, -movement.lng, -movement.alt));
    }


    @Override
    public void evaluateArithmetic (int op, Data data) {
        Position d = (Position) data;
        switch (op) {
            case HelixLexer.PLUS:
                lat += d.lat;
                lng += d.lng;
                alt += d.alt;
                break;
            case HelixLexer.MINUS:
                lat -= d.lat;
                lng -= d.lng;
                alt -= d.alt;
                break;
            case HelixLexer.MUL:
                lat *= d.lat;
                lng *= d.lng;
                alt *= d.alt;
                break;
            case HelixLexer.DIV:
                lat /= d.lat;
                lng /= d.lng;
                alt /= d.alt;
                break;
            default: assert false;
        }
    }


    @Override
    public BoolData evaluateRelational (int op, Data data) {
        Position d = (Position) data;
        switch (op) {
            case HelixLexer.EQUAL: 
                return new BoolData(
                        lat >= d.lat - epsilon && lat <= d.lat + epsilon &&
                        lng >= d.lng - epsilon && lng <= d.lng + epsilon &&
                        alt >= d.alt - epsilon && alt <= d.alt + epsilon 
                );
            case HelixLexer.NOT_EQUAL: 
                return new BoolData(
                        lat < d.lat - epsilon || lat > d.lat + epsilon ||
                        lng < d.lng - epsilon || lng > d.lng + epsilon ||
                        alt < d.alt - epsilon || alt > d.alt + epsilon
                );
            default: assert false; 
        }
        return null;
    }


    @Override
    public Data getCopy() {
        Position copy = new Position(this);
        return copy;
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


    @Override
    public String toString() {
        return toString(false);
    }
}
