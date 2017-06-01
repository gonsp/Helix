package Helix.interpreter.controller;


import Helix.interpreter.GPSPosition;
import Helix.interpreter.Position;
import org.omg.PortableServer.POA;

public abstract class DroneController {

    private Drone drone;
    protected GPSPosition homeLocation;

    public DroneController() {
        drone = new Drone();
    }

    public void init() {
        homeLocation = getGPS();
        drone.direction = getDirection();
        System.out.println("DroneController initialized, with home position: " + homeLocation.toString() + " and direction: " + drone.direction + "º");
    }

    public void setDirection(double direction) {
        direction %= 360;
        if(direction < 0) {
            direction += 360;
        }
        sendDirection(direction);
        drone.direction = direction;
    }

    public void lookAt(Position pos) {
        pos.move(drone.pos);
        setDirection(Math.toDegrees(Math.atan(pos.lng/pos.lat)));
    }

    public void rotate(double degrees) {
        setDirection(drone.direction + degrees);
    }

    public void forward(double dist) {
        double radians = Math.toRadians(drone.direction);
        double n = Math.cos(radians) * dist;
        double e = Math.sin(radians) * dist;
        move(new Position(n, e, 0));
    }

    public void backward(double dist) {
        forward(-dist);
    }

    public void right(double dist) {
        double radians = Math.toRadians(drone.direction+90);
        double n = Math.cos(radians) * dist;
        double e = Math.sin(radians) * dist;
        move(new Position(n, e, 0));
    }

    public void left(double dist) {
        right(-dist);
    }

    public void up(double dist) {
        move(new Position(0, 0, dist));
    }

    public void down(double dist) {
        up(-dist);
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
        drone.isLanded = false; //If the autonomous flight starts in the middle of a fly, the drone is marked as "fliying"
    }

    public void takeOff(double height) { //LibrePilot default take off is 2.5 m
        if(drone.isLanded && height > 2.5) { //Security margin
            sendTakeOff(height);
            drone.pos = new Position(0, 0, height);
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

    public Position getPos() {
        return new Position(drone.pos);
    }

    public abstract GPSPosition getGPS(); //Returns de gps position of the drone
    public abstract double getDirection(); //Returns de orientation in degrees of the drone. 0º is Nort, 180º is South, etc
    protected abstract void sendTakeOff(double height);
    protected abstract void sendMoveTo(GPSPosition pos);
    protected abstract void sendDirection(double direction);
    protected abstract void sendLand();
}
