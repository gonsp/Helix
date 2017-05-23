package Helix.interpreter.controller;


import Helix.interpreter.GPSPosition;
import Helix.interpreter.Position;

public abstract class DroneController {

    private Drone drone;
    private GPSPosition homeLocation;

    protected DroneController() {
        drone = new Drone(getGPS());
        homeLocation = getGPS();
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
        drone.pos = newPos;
    }

    public void moveTo(Position pos) {
        sendMoveTo(pos.toGPS(homeLocation));
        drone.pos = pos;
    }

    public void land() {
        if(!drone.isLanded) {
            sendLand();
            drone.isLanded = true;
        }
    }

    public abstract GPSPosition getGPS();
    protected abstract void sendMoveTo(GPSPosition pos);
    protected abstract void sendLand();
}
