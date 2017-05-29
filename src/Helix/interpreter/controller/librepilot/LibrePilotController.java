package Helix.interpreter.controller.librepilot;

import Helix.interpreter.GPSPosition;
import Helix.interpreter.Position;
import Helix.interpreter.controller.DroneController;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkMissingObjectException;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObject;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObjectListener;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcDevice;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcUsbDevice;

public class LibrePilotController extends DroneController implements PathPlanListener, GPSListener, UAVTalkObjectListener {

    private PathPlanManager pathPlanManager;
    private GPSManager gpsManager;

    volatile private boolean onAutonomousMode;
    volatile private boolean isArmed;

    volatile private GPSPosition posGPS;
    volatile private boolean onAction;

    private static final String UAVO_NAME = "FlightStatus";
    private static final int MIN_SATELLITES = 6;
    private static final double DEFAULT_VELOCITY = 2;

    public LibrePilotController() {
        FcDevice device = new FcUsbDevice();
        device.start();

        pathPlanManager = new PathPlanManager(this, device);
        gpsManager = new GPSManager(this, device, MIN_SATELLITES);

/*        // TESTING
        GPSPosition homeLocation = new GPSPosition(41.1, 2.1, 170);
        Position pos = new Position(0, 0, 1000);
        GPSPosition gpsPos = new GPSPosition(homeLocation);
        gpsPos.move(pos);
        pathPlanManager.sendMoveTo(gpsPos, homeLocation, 3);

        System.exit(0);*/

        posGPS = null;
        System.out.println("Waiting to get a gps position");
        while(posGPS == null);
        System.out.println("Waiting to get a good enough gps position (" + MIN_SATELLITES + " satellites)");
        gpsManager.clearHomePosition();

        isArmed = false;
        onAutonomousMode = false;
        device.requestObject(UAVO_NAME);
        device.setListener(UAVO_NAME, this);
        System.out.println("Waiting to enter en autonomous mode");
        while(!onAutonomousMode);
        System.out.println("On autonomous mode");
        if(!isArmed) {
            System.out.println("Drone is not armed. Aborting");
            System.exit(0);
        }

        System.out.println("LibrePilotController is ready");
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
        try {
            String flightMode = (String) o.getData("FlightMode");
            if(flightMode == null) {
                System.exit(1);
            }
            boolean isAutonomous = flightMode.equals("PathPlanner");
            if(onAutonomousMode && !isAutonomous) {
                onAutonomousMode = false;
                System.err.println("Pilot has changed to " + flightMode + " (manual control). Finishing program");
                System.exit(0);
            } else {
                onAutonomousMode = isAutonomous;
            }

            isArmed = o.getData("Armed").equals("Armed");
        } catch (UAVTalkMissingObjectException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}