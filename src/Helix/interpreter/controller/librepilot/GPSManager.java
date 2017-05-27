package Helix.interpreter.controller.librepilot;


import Helix.interpreter.GPSPosition;
import Helix.interpreter.controller.librepilot.uavtalk.*;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcDevice;

public class GPSManager implements UAVTalkObjectListener {

    private static final String UAVO_NAME = "GPSPositionSensor";

    private FcDevice device;
    private GPSListener listener;
    private int minSatellites;
    private volatile int act_sat;

    public GPSManager(GPSListener listener, FcDevice device, int minSatellites) {
        this.listener = listener;
        this.device = device;
        this.minSatellites = minSatellites;
        act_sat = 0;
        device.requestObject(UAVO_NAME);
        device.setListener(UAVO_NAME, this);
    }

    void clearHomePosition() {
        while(act_sat < minSatellites);
        System.out.println("Min number satellites satisfied. Act sat: " + act_sat);
        byte[] data = new byte[1];
        data[0] = 0;
        device.sendSettingsObject("HomeLocation", 0, "Set", "0", data);
        device.savePersistent("HomeLocation");

        String set;
        try {
            set = (String) device.getObjectTree().getObjectFromName("HomeLocation").getData("Set");
            if(!set.equals("False")) {
                System.err.println("HomeLocation not cleaned");
                System.exit(1);
            }
            while(set == null || set.equals("False")) {
                set = (String) device.getObjectTree().getObjectFromName("HomeLocation").getData("Set");
            }
            System.out.println("HomeLocation cleaned successfully");
        } catch (UAVTalkMissingObjectException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void onObjectUpdate(UAVTalkObject o) {
        try {
            int new_act_sat = (Integer) o.getData("Satellites");
            if(new_act_sat != act_sat) {
                System.out.println("Num used satellites = " + new_act_sat);
                act_sat = new_act_sat;
            }
            double lat = (Integer) o.getData("Latitude");
            double lng = (Integer) o.getData("Longitude");
            double alt = (Float) o.getData("Altitude");
            lat /= 10000000;
            lng /= 10000000;
            listener.onGPSUpdate(new GPSPosition(lat, lng, alt));
        } catch (UAVTalkMissingObjectException e) {
            e.printStackTrace();
        }
    }
}