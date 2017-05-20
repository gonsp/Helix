package Helix.interpreter.librepilot;

import Helix.interpreter.librepilot.uavtalk.UAVTalkMissingObjectException;
import Helix.interpreter.librepilot.uavtalk.UAVTalkObject;
import Helix.interpreter.librepilot.uavtalk.UAVTalkObjectListener;
import Helix.interpreter.librepilot.uavtalk.device.FcDevice;

public class PathPlannerManager implements UAVTalkObjectListener {

    private static final String UAOV_NAME = "AttitudeState";

    private PathPlannerListener listener;



    private FcDevice device;

    public PathPlannerManager(PathPlannerListener listener, FcDevice device) {
        this.listener = listener;
        this.device = device;
        device.requestObject(UAOV_NAME);
        device.setListener(UAOV_NAME, this);
    }

    @Override
    public void onObjectUpdate(UAVTalkObject o) {
        System.out.print("DATA UPDATED: ");
        try {
            System.out.println(device.getObjectTree().getData(UAOV_NAME, "Roll"));
        } catch (UAVTalkMissingObjectException e) {
            e.printStackTrace();
        }
    }
}
