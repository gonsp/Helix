package Helix.interpreter.controller.librepilot;

import Helix.interpreter.GPSPosition;
import Helix.interpreter.Position;
import Helix.interpreter.controller.librepilot.uavtalk.*;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcDevice;

public class Waypoint extends Position {

    public double velocity;
    public double progress;

    protected byte pathActionType;

    public Waypoint(GPSPosition pos, GPSPosition homePosition, double velocity) {
        this(pos.toRelative(homePosition), velocity);
    }

    public Waypoint(Position pos, double velocity) {
        super(pos);
        this.velocity = velocity;
        progress = 0;
        pathActionType = 0;
    }

    public void upload(FcDevice device) {
        update(device, false);
    }

    public void delete(FcDevice device) {
        update(device, true);
    }

    private void update(FcDevice device, boolean clear) {
        byte total_data[] = new byte[0];
        total_data = Utils.concatArray(total_data, updateWaypoint(device, clear));
        total_data = Utils.concatArray(total_data, updatePathAction(device, clear));

        byte crc = (byte) Utils.crc8(total_data, 0, total_data.length);
        updatePathPlan(device, clear, crc);
        System.out.println("Waypoint updated");
    }

    private byte[] updatePathAction(FcDevice device, boolean clear) {
        byte data[] = new byte[device.getObjectTree().getXmlObjects().get("PathAction").getLength()];
        UAVTalkObjectInstance instance = new UAVTalkObjectInstance(0, data);
        UAVTalkObject pathActions = device.getObjectTree().getObjectFromName("PathAction");
        pathActions.setInstance(instance);
        if(!clear) {
            byte[] mode = {pathActionType};
            device.sendSettingsObject("PathAction", 0, "Mode", 0, mode);
        } else {
            device.sendSettingsObject("PathAction", 0);
        }
        return pathActions.getInstance(0).getData();
    }

    private byte[] updateWaypoint(FcDevice device, boolean clear) {
        byte data[] = new byte[device.getObjectTree().getXmlObjects().get("Waypoint").getLength()];
        if(!clear) {
            data = fillField(data, lat, 0);
            data = fillField(data, lng, 1);
            data = fillField(data, -alt, 2);
            data = fillField(data, velocity, 3);
        }
        //Adding the new instance
        UAVTalkObjectInstance instance = new UAVTalkObjectInstance(0, data);
        UAVTalkObject waypoints = device.getObjectTree().getObjectFromName("Waypoint");
        waypoints.setInstance(instance);
        device.sendSettingsObject("Waypoint", 0);
        return data;
    }

    private byte[] fillField(byte data[], double value, int pos) {
        return Utils.writeInArray(Utils.floatToByteArrayRev((float) value), data, pos * 4);
    }

    private void updatePathPlan(FcDevice device, boolean clear, byte crc) {
        short waypointCount = (short) (clear ? 0 : 1);
        short pathActionCount = (short) (clear ? 0 : 1);

        byte data[] = new byte[device.getObjectTree().getXmlObjects().get("PathPlan").getLength()];
        data = Utils.writeInArray(Utils.toReversedBytes(waypointCount), data, 0);
        data = Utils.writeInArray(Utils.toReversedBytes(pathActionCount), data, 2);
        data[4] = crc;
        UAVTalkObjectInstance instance = new UAVTalkObjectInstance(0, data);
        UAVTalkObject pathPlan = device.getObjectTree().getObjectFromName("PathPlan");
        pathPlan.setInstance(instance);
        device.sendSettingsObject("PathPlan", 0);
    }
}
