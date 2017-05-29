package Helix.interpreter.controller.librepilot;


import Helix.interpreter.Position;

public class TakeOffWaypoint extends Waypoint {

    public TakeOffWaypoint(double height, double velocity) {
        super(new Position(0, 0, height), velocity);
        pathActionType = 0;
    }
}
