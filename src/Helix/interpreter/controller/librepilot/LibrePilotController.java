package Helix.interpreter.controller.librepilot;

import Helix.interpreter.GPSPosition;
import Helix.interpreter.controller.DroneController;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkMissingObjectException;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObject;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObjectListener;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcDevice;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcUsbDevice;

public class LibrePilotController extends DroneController implements PathPlanListener, GPSListener, UAVTalkObjectListener {

    private PathPlanManager pathPlanManager;
    private GPSManager gpsManager;

    volatile private boolean onAutomaticMode;
    volatile private GPSPosition posGPS;
    volatile private boolean onAction;

    private static final String UAVO_NAME = "FlightStatus";
    private static final int MIN_SATELLITES = 14;
    private static final double DEFAULT_VELOCITY = 3;

    public LibrePilotController() {
        FcDevice device = new FcUsbDevice();
        device.start();

        device.requestMetaObject(UAVO_NAME);
        device.setListener(UAVO_NAME, this);
        onAutomaticMode = false;
        while(onAutomaticMode);
        System.out.println("On automatic mode. Starting");

        pathPlanManager = new PathPlanManager(this, device);
        gpsManager = new GPSManager(this, device, MIN_SATELLITES);

        posGPS = null;
        while(posGPS == null);
        gpsManager.clearHomePosition();
    }

    @Override
    public void sendMoveTo(GPSPosition pos) {
        onAction = true;
        pathPlanManager.sendMoveTo(pos, super.homeLocation, DEFAULT_VELOCITY);
        while(onAction);
    }

    @Override
    public void sendLand() {
        onAction = true;
        pathPlanManager.sendLand();
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

    @Override
    public void onObjectUpdate(UAVTalkObject o) {
        //If the flight mode is not pathplanner, it means that the pilot has changed to manual control, so aborting program
        String flightMode = null;
        try {
            flightMode = (String) o.getData("FlightMode");
            if(flightMode == null) {
                System.exit(1);
            }
            boolean isAutomatic = flightMode.equals("PathPlanner");
            if(onAutomaticMode && !isAutomatic) {
                onAutomaticMode = false;
                System.err.println("Pilot has changed to manual control. Finishing program");
                System.exit(0);
            } else {
                onAutomaticMode = isAutomatic;
            }
        } catch (UAVTalkMissingObjectException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}