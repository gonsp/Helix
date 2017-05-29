package Helix.interpreter.controller.librepilot;

import Helix.interpreter.GPSPosition;
import Helix.interpreter.Position;
import Helix.interpreter.controller.librepilot.uavtalk.*;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcDevice;

public class Waypoint extends Position {

    public double velocity;
    public double progress;

    public Waypoint(GPSPosition pos, GPSPosition homePosition, double velocity) {
        super(pos.toRelative(homePosition));
        this.velocity = velocity;
        progress = 0;
    }

    public Waypoint(Waypoint waypoint) {
        super(waypoint);
        this.velocity = waypoint.velocity;
        progress = 0;
    }

    public void upload(FcDevice device) {
        byte total_data[] = new byte[0];
        total_data = Utils.concatArray(total_data, uploadWaypoint(device, this, 0));
        Waypoint holdPos = new Waypoint(this); // This is an auxiliar waypoint which will come after this one in order to detect when the original ends
        holdPos.velocity = 0;
        holdPos.move(new Position(10, 10, 10)); // To move it from the final position of the drone, so the progress won't be 100%
        total_data  = Utils.concatArray(total_data, uploadWaypoint(device, holdPos, 1));

        total_data = Utils.concatArray(total_data, uploadPathAction(device));

        byte crc = (byte) Utils.crc8(total_data, 0, total_data.length);
        updatePathPlan(device, crc);

        System.out.println("Waypoint uploaded");
    }

    private byte[] uploadPathAction(FcDevice device) {
        byte data[] = new byte[device.getObjectTree().getXmlObjects().get("PathAction").getLength()];
        UAVTalkObjectInstance instance = new UAVTalkObjectInstance(0, data);
        UAVTalkObject pathActions = device.getObjectTree().getObjectFromName("PathAction");
        pathActions.setInstance(instance);
        device.sendSettingsObject("PathAction", 0);
        return data;
    }

    private byte[] uploadWaypoint(FcDevice device, Waypoint waypoint, int id) {
        byte data[] = new byte[device.getObjectTree().getXmlObjects().get("Waypoint").getLength()];
        data = fillField(data, waypoint.lat, 0);
        data = fillField(data, waypoint.lng, 1);
        data = fillField(data, -waypoint.alt, 2);
        data = fillField(data, waypoint.velocity, 3);
        //Adding the new instance
        UAVTalkObjectInstance instance = new UAVTalkObjectInstance(id, data);
        UAVTalkObject waypoints = device.getObjectTree().getObjectFromName("Waypoint");
        waypoints.setInstance(instance);
        device.sendSettingsObject("Waypoint", id);
        return data;
    }

    private byte[] fillField(byte data[], double value, int pos) {
        return Utils.writeInArray(Utils.floatToByteArrayRev((float) value), data, pos * 4);
    }

    private void updatePathPlan(FcDevice device, byte crc) {
        byte data[] = new byte[device.getObjectTree().getXmlObjects().get("PathPlan").getLength()];
        data = Utils.writeInArray(Utils.toReversedBytes((short) 2), data, 0);
        data = Utils.writeInArray(Utils.toReversedBytes((short) 1), data, 2);
        data[4] = crc;
        UAVTalkObjectInstance instance = new UAVTalkObjectInstance(0, data);
        UAVTalkObject pathPlan = device.getObjectTree().getObjectFromName("PathPlan");
        pathPlan.setInstance(instance);
        device.sendSettingsObject("PathPlan", 0);
    }

    public void delete() {
        // TODO delete waypoints from device
    }
}
