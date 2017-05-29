package Helix.interpreter.controller.librepilot;

import Helix.interpreter.GPSPosition;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkMissingObjectException;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObject;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObjectListener;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcDevice;

public class PathPlanManager implements UAVTalkObjectListener {

    private static final String PATH_STATUS = "PathStatus";
    private static final String ACTIVE_WAYPOINT = "WaypointActive";

    private PathPlanListener listener;
    private FcDevice device;
    private volatile Waypoint activeWaypoint;

    public PathPlanManager(PathPlanListener listener, FcDevice device) {
        this.listener = listener;
        this.device = device;
        activeWaypoint = null;

        device.requestObject(PATH_STATUS);
        device.setListener(PATH_STATUS, this);
        device.setListener(ACTIVE_WAYPOINT, this);
    }

    public void sendMoveTo(GPSPosition position, GPSPosition homeLocation, double velocity) {
        activeWaypoint = new Waypoint(position, homeLocation, velocity);
        activeWaypoint.upload(device);
        while(activeWaypoint != null) {
            device.requestObject(ACTIVE_WAYPOINT);
        }
    }

    public void sendLand() {
        //TODO implement this
    }

    @Override
    public void onObjectUpdate(UAVTalkObject o) {
        if(activeWaypoint != null) {
            try {
                if(o.getId().equals(device.getObjectTree().getXmlObjects().get(PATH_STATUS).getId())) {
                    float progress = (float) o.getData("fractional_progress");
                    activeWaypoint.progress = progress;
                    listener.onProgressUpdate(progress);
                    int error = (int) o.getData("Status");
                    if(error != 3) {
                        listener.onError();
                    }
                } else {
                    if(o == null) {
                        System.out.println("WTF");
                    }
                    int active_index = (int) o.getData("Index");
                    if(active_index == 1) {
                        activeWaypoint = null;
                        listener.onFinishPath();
                        System.out.println("Waypoint: " + active_index);
                        System.out.println("----------------------------------------------");
                    }
                }
            } catch (UAVTalkMissingObjectException e) {
                e.printStackTrace();
            }
        }
    }
}
