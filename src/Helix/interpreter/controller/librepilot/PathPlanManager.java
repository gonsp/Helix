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

        device.requestObject(PATH_STATUS);
        device.setListener(PATH_STATUS, this);
        device.requestObject(ACTIVE_WAYPOINT);
        device.setListener(ACTIVE_WAYPOINT, this);
    }

    public void sendMoveTo(GPSPosition position, GPSPosition homeLocation, double velocity) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        activeWaypoint = new Waypoint(position, homeLocation, velocity);
        activeWaypoint.upload(device);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        UAVTalkObjectListener listener = new UAVTalkObjectListener() {
            @Override
            public void onObjectUpdate(UAVTalkObject o) {
                try {
                    System.out.println("Pos waypoint 1: " + o.getData(0, "Position", 0) + ", " + o.getData(0, "Position", 1) + ", " + o.getData(0, "Position", 2));
                    System.out.println("Pos waypoint 2: " + o.getData(1, "Position", 0) + ", " + o.getData(1, "Position", 1) + ", " + o.getData(1, "Position", 2));
                } catch (UAVTalkMissingObjectException e) {
                    e.printStackTrace();
                }
            }
        };
        device.setListener("Waypoint", listener);
        device.requestObject(ACTIVE_WAYPOINT);
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
                    /*float error = (float) o.getData("error");
                    if(error != 0) {
                        listener.onError();
                    }*/
                    activeWaypoint.progress = progress;
                    listener.onProgressUpdate(progress);
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
                        System.exit(0);
                    } else {
                        device.requestObject(ACTIVE_WAYPOINT);
                    }
                }
            } catch (UAVTalkMissingObjectException e) {
                e.printStackTrace();
            }
        }
    }
}
