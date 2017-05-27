package Helix.interpreter.controller.librepilot;

import Helix.interpreter.GPSPosition;
import Helix.interpreter.Position;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObject;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObjectInstance;
import Helix.interpreter.controller.librepilot.uavtalk.device.FcDevice;

import java.nio.ByteBuffer;

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
        upload(device, this, 0);
        Waypoint holdPos = new Waypoint(this); // This is an auxiliar waypoint which will come after this one in order to detect when the original ends
        holdPos.velocity = 0;
        holdPos.move(new Position(10, 10, 10)); // To move it from the final position of the drone, so the progress won't be 100%
        upload(device, holdPos, 1);
    }

    private void upload(FcDevice device, Waypoint waypoint, int id) {
        byte data[] = new byte[device.getObjectTree().getXmlObjects().get("Waypoint").getLength()];
        //Adding the new instance
        UAVTalkObjectInstance instance = new UAVTalkObjectInstance(id, data);
        UAVTalkObject waypoints = device.getObjectTree().getObjectFromName("Waypoint");
        waypoints.setInstance(instance);

        //Setting the position of the new instance
        sendPositionField(device, id, "Position", 0, waypoint.lat);
        sendPositionField(device, id, "Position", 1, waypoint.lng);
        sendPositionField(device, id, "Position", 2, -waypoint.alt);
        sendPositionField(device, id, "Velocity", 0, waypoint.velocity);
    }

    private void sendPositionField(FcDevice device, int instanceID, String fieldName, int elementID, double value) {
        byte fieldData[] = ByteBuffer.allocate(4).putFloat((float) value).array();
        device.sendSettingsObject("Waypoint", instanceID, fieldName, elementID, fieldData);
    }


    public void delete() {
        // TODO delete waypoints from device
    }
}
