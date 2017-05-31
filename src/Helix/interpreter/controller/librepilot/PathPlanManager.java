package Helix.interpreter.controller.librepilot;

import Helix.interpreter.GPSPosition;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkMissingObjectException;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObject;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObjectListener;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcDevice;

public class PathPlanManager implements UAVTalkObjectListener {

    private static final String PATH_STATUS = "PathStatus";

    private PathPlanListener listener;
    private FcDevice device;
    private volatile Waypoint activeWaypoint;

    public PathPlanManager(PathPlanListener listener, FcDevice device) {
        this.listener = listener;
        this.device = device;
        activeWaypoint = null;

        device.requestObject(PATH_STATUS);
        device.setListener(PATH_STATUS, this);
    }

    public void sendTakeOff(double height, double velocity) {
        activeWaypoint = new TakeOffWaypoint(height, velocity);
        activeWaypoint.upload(device);
    }

    public void sendMoveTo(GPSPosition position, GPSPosition homeLocation, double velocity) {
        activeWaypoint = new Waypoint(position, homeLocation, velocity);
        activeWaypoint.upload(device);
    }

    public void sendLand(double velocity) {
        activeWaypoint = new LandWaypoint(velocity);
        activeWaypoint.upload(device);
    }

    @Override
    public void onObjectUpdate(UAVTalkObject o) {
        if(activeWaypoint != null) {
            try {
                float progress = (float) o.getData("fractional_progress");
                activeWaypoint.progress = progress;
                listener.onProgressUpdate(progress);
                if(o.getData("Status").equals("Critical")) {
                    listener.onError();
                }
                if(progress > 0.85) {
                    activeWaypoint.delete(device);
                    activeWaypoint = null;
                    listener.onFinishPath();
                }
            } catch (UAVTalkMissingObjectException e) {
                e.printStackTrace();
            }
        }
    }
}
