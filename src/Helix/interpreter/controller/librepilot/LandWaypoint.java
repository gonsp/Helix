package Helix.interpreter.controller.librepilot;

import Helix.interpreter.Position;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcDevice;

public class LandWaypoint extends Waypoint {

    public LandWaypoint(double velocity) {
        super(new Position(0, 0, 0), velocity);
        pathActionType = 7;
    }

    @Override
    public void delete(FcDevice device) {
        // A land waypoint should not be removed
    }
}
