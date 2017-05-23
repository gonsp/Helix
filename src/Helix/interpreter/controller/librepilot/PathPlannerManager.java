package Helix.interpreter.controller.librepilot;

import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkMissingObjectException;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObject;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObjectListener;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcDevice;

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
        System.out.print("PATH UPDATED: ");
        try {
            System.out.println(o.getData(UAOV_NAME, "WaypointCount"));
        } catch (UAVTalkMissingObjectException e) {
            e.printStackTrace();
        }
    }
}
