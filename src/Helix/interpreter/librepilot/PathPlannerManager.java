package Helix.interpreter.librepilot;

import Helix.interpreter.librepilot.uavtalk.UAVTalkObject;
import Helix.interpreter.librepilot.uavtalk.UAVTalkObjectListener;
import Helix.interpreter.librepilot.uavtalk.device.FcDevice;

public class PathPlannerManager implements UAVTalkObjectListener {

    private static final String UAOV_NAME = "AttitudeState";

    private PathPlannerListener listener;

    public PathPlannerManager(PathPlannerListener listener, FcDevice device) {
        this.listener = listener;
        device.requestObject(UAOV_NAME);
        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        device.setListener(UAOV_NAME, this);
    }

    @Override
    public void onObjectUpdate(UAVTalkObject o) {
        System.out.println("HELLO");
    }
}
