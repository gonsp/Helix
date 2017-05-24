package Helix.interpreter.controller;


import Helix.interpreter.GPSPosition;
import Helix.interpreter.Position;

public abstract class DroneController {

    private Drone drone;
    protected GPSPosition homeLocation;

    public DroneController() {
        drone = new Drone();
    }

    public void init() {
        homeLocation = getGPS();
    }

    public void lookAt(Position pos) {
        //TODO implement this
    }

    public void setDirection(double degrees) {
        roll(drone.direction - degrees);
        drone.direction = degrees;
    }

    public void roll(double degrees) {
        //TODO implement this
        //FIXME be careful with the module and negative values
        drone.direction += degrees%360;
    }

    public void forward(double dist) {
        // TODO implement this
    }

    public void backward(double dist) {
        forward(-dist);
    }

    public void right(double dist) {
        // TODO implement this
    }

    public void up(double dist) {
        move(new Position(0, 0, dist));
    }

    public void down(double dist) {
        up(-dist);
    }

    public void left(double dist) {
        right(-dist);
    }

    public void north(double dist) {
        move(new Position(dist, 0, 0));
    }

    public void south(double dist) {
        north(-dist);
    }

    public void east(double dist) {
        move(new Position(0, dist, 0));
    }

    public void west(double dist) {
        east(-dist);
    }

    private void move(Position movement) {
        Position newPos = new Position(drone.pos);
        newPos.move(movement);
        moveTo(newPos);
    }

    public void moveTo(Position pos) {
        sendMoveTo(pos.toGPS(homeLocation));
        drone.pos = pos;
    }

    public void takeOff(double height) {
        // TODO add exceptions
        if(drone.isLanded && height > 0) {
            move(new Position(0, 0, height));
            drone.isLanded = false;
        }
    }

    public void land() {
        if(!drone.isLanded) {
            sendLand();
            drone.isLanded = true;
            drone.pos = getGPS().toRelative(homeLocation);
        }
    }

    public abstract GPSPosition getGPS();
    protected abstract void sendMoveTo(GPSPosition pos);
    protected abstract void sendLand();
}
