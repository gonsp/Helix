package Helix.interpreter.librepilot;

import Helix.interpreter.DroneController;
import Helix.interpreter.librepilot.uavtalk.UAVTalkXMLObject;
import Helix.interpreter.librepilot.uavtalk.device.FcDevice;
import Helix.interpreter.librepilot.uavtalk.device.FcUsbDevice;

public class LibrePilotController extends DroneController implements PathPlannerListener {

    private PathPlannerManager pathPlannerManager;

    public LibrePilotController() {
        FcDevice device = new FcUsbDevice();
        device.start();

        pathPlannerManager = new PathPlannerManager(this, device);

    }

    @Override
    public void moveTo() {

    }

    @Override
    public void onProgressUpdate(float progress) {

    }

    @Override
    public void onFinishPath() {

    }

    @Override
    public void onError() {

    }
}