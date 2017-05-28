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
    private Waypoint activeWaypoint;
    private FcDevice device;

    public PathPlanManager(PathPlanListener listener, FcDevice device) {
        this.listener = listener;
        this.device = device;
        activeWaypoint = null;

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        device.requestObject(PATH_STATUS);
        device.setListener(PATH_STATUS, this);
        device.requestObject(ACTIVE_WAYPOINT);
        device.setListener(ACTIVE_WAYPOINT, this);
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
            try {
                if(o.getId().equals(device.getObjectTree().getXmlObjects().get(PATH_STATUS).getId())) {
                    float progress = (float) o.getData("fractional_progress");
                    float error = (float) o.getData("error");
                    if(error != 0) {
                        listener.onError();
                    }
                    activeWaypoint.progress = progress;
                    listener.onProgressUpdate(progress);
                } else {
                    int active_index = (int) o.getData("Index");
                    if(active_index == 1) {
                        activeWaypoint = null;
                        listener.onFinishPath();
                    }
                    System.out.println("Waypoint: " + active_index);
                }
            } catch (UAVTalkMissingObjectException e) {
                e.printStackTrace();
            }
        }
    }
}
