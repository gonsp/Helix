package Helix.interpreter.librepilot;

import Helix.interpreter.DroneController;
import Helix.interpreter.Position;
import Helix.interpreter.librepilot.uavtalk.UAVTalkXMLObject;
import Helix.interpreter.librepilot.uavtalk.device.FcDevice;
import Helix.interpreter.librepilot.uavtalk.device.FcUsbDevice;

public class LibrePilotController extends DroneController implements PathPlannerListener {

    private PathPlannerManager pathPlannerManager;

    volatile private boolean onAction;

    public LibrePilotController() {
        FcDevice device = new FcUsbDevice();
        device.start();

        pathPlannerManager = new PathPlannerManager(this, device);

    }

    @Override
    public void moveTo(Position pos) {
        onAction = true;

        while(onAction);
    }

    @Override
    public void land() {

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
}