package Helix.interpreter.controller.librepilot;

import Helix.interpreter.GPSPosition;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObject;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObjectListener;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcDevice;

public class PathPlanManager implements UAVTalkObjectListener {

    private static final String UAVO_NAME = "PathStatus";

    private PathPlanListener listener;
    private Waypoint activeWaypoint;
    private FcDevice device;

    public PathPlanManager(PathPlanListener listener, FcDevice device) {
        this.listener = listener;
        this.device = device;
        activeWaypoint = null;
        device.requestObject(UAVO_NAME);
        device.setListener(UAVO_NAME, this);
    }

    public void sendMoveTo(GPSPosition position, GPSPosition homeLocation, double velocity) {
        activeWaypoint = new Waypoint(position, homeLocation, velocity);
        activeWaypoint.upload(device);
    }

    public void sendLand() {

    }

    @Override
    public void onObjectUpdate(UAVTalkObject o) {
        if(activeWaypoint != null) {
            System.out.println("PATH UPDATED: ");
/*            if(ends) {
                activeWaypoint.delete();
                activeWaypoint = null;
                listener.onFinishPath();
            } else if(progress) {
                activeWaypoint.progress = progress;
                listener.onProgressUpdate(progress);
            } else if(error) {
                listener.onError();
            }
*/
        }
    }
}
