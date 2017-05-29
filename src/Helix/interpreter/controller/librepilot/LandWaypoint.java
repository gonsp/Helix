package Helix.interpreter.controller.librepilot;

import Helix.interpreter.Position;

public class LandWaypoint extends Waypoint {

    public LandWaypoint(double velocity) {
        super(new Position(0, 0, 0), velocity);
        pathActionType = 7;
    }
}
