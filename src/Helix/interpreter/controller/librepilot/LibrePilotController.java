package Helix.interpreter.controller.librepilot;

import Helix.interpreter.GPSPosition;
import Helix.interpreter.controller.DroneController;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcDevice;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcUsbDevice;

public class LibrePilotController extends DroneController implements PathPlannerListener, GPSListener {

    private PathPlannerManager pathPlannerManager;
    private GPSManager gpsManager;

    volatile private GPSPosition posGPS;
    volatile private boolean onAction;

    private static final int MIN_SATELLITES = 16;

    public LibrePilotController() {
        FcDevice device = new FcUsbDevice();
        device.start();

        pathPlannerManager = new PathPlannerManager(this, device);
        gpsManager = new GPSManager(this, device, MIN_SATELLITES);

        posGPS = null;
        while(posGPS == null);
        gpsManager.clearHomePosition();
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
    public GPSPosition getGPS() {
        return new GPSPosition(posGPS);
    }

    @Override
    public void onGPSUpdate(GPSPosition newPos) {
        posGPS = newPos;
    }

    @Override
    public void onProgressUpdate(float progress) {
        System.out.println("Progress of actual instruction: " + progress*100 + "%");
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