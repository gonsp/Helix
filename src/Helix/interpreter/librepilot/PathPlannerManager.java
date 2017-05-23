package Helix.interpreter.librepilot;

import Helix.interpreter.librepilot.uavtalk.UAVTalkMissingObjectException;
import Helix.interpreter.librepilot.uavtalk.UAVTalkObject;
import Helix.interpreter.librepilot.uavtalk.UAVTalkObjectListener;
import Helix.interpreter.librepilot.uavtalk.device.FcDevice;

public class PathPlannerManager implements UAVTalkObjectListener {

    private static final String UAOV_NAME = "PathPlan";

    private PathPlannerListener listener;

    public PathPlannerManager(PathPlannerListener listener, FcDevice device) {
        this.listener = listener;
        device.requestObject(UAOV_NAME);
        device.setListener(UAOV_NAME, this);
    }

    @Override
    public void onObjectUpdate(UAVTalkObject o) {
        System.out.print("DATA UPDATED: ");
        try {
            System.out.println(o.getData(UAOV_NAME, "WaypointCount"));
        } catch (UAVTalkMissingObjectException e) {
            e.printStackTrace();
        }
    }
}
