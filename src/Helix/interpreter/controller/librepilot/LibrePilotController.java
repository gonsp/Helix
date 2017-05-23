package Helix.interpreter.controller.librepilot;

import Helix.interpreter.controller.DroneController;
import Helix.interpreter.GPSPosition;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkMissingObjectException;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObject;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObjectListener;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcDevice;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcUsbDevice;

public class LibrePilotController extends DroneController implements PathPlannerListener, UAVTalkObjectListener {

    private PathPlannerManager pathPlannerManager;

    volatile private GPSPosition posGPS;
    volatile private boolean onAction;

    private static final String UAOV_NAME = "GPSPositionSensor";

    public LibrePilotController() {
        FcDevice device = new FcUsbDevice();
        device.start();

        pathPlannerManager = new PathPlannerManager(this, device);

        // TODO Clear homelocation
        posGPS = null;
        while(posGPS == null);
        // TODO Wait for homelocation
    }

    @Override
    public GPSPosition getGPS() {
        return posGPS;
    }

    @Override
    public void sendMoveTo(GPSPosition pos) {
        onAction = true;

        while(onAction);
    }

    @Override
    public void sendLand() {
        onAction = true;

        while(onAction);
    }



    @Override
    public void onProgressUpdate(float progress) {

    }

    @Override
    public void onFinishPath() {
        onAction = false;
    }

    @Override
    public void onError() {
        onAction = false;
        System.out.println("Error flying. Aborting");
        System.exit(1);
    }

    @Override
    public void onObjectUpdate(UAVTalkObject o) {
        // TODO implement this
        // FIXME use positionstate.xml + homeposition
        // FIXME maybe is better to create a GPSManager (as PathPlanner)
        System.out.print("GPS UPDATED: ");
        try {
            System.out.println(o.getData(UAOV_NAME, "Latitude"));
            System.out.println(o.getData(UAOV_NAME, "Longitude"));
            System.out.println(o.getData(UAOV_NAME, "Altitude"));
        } catch (UAVTalkMissingObjectException e) {
            e.printStackTrace();
        }
    }
}