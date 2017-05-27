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

    public void upload(FcDevice device) {
        upload(device, this, 0);
        Position holdPos = new Position(this); // This is an auxiliar waypoint which will come after this one in order to detect when the original ends
        holdPos.move(new Position(10, 10, 10)); // To move it from the final position of the drone, so the progress won't be 100%
        upload(device, holdPos, 1);
    }

    private void upload(FcDevice device, Position pos, int id) {
        //Cloning the "default" data from the instance 1
        byte data[] = device.getObjectTree().getObjectFromID("Waypoint").getInstance(0).getData().clone();
        //Adding the new instance (or replacing the default one)
        UAVTalkObjectInstance instance = new UAVTalkObjectInstance(id, data);
        UAVTalkObject waypoints = device.getObjectTree().getObjectFromName("Waypoint");
        waypoints.setInstance(instance);
        //Setting the position of the new instance

        sendPositionField(device, id, 0, super.lat);
        sendPositionField(device, id, 1, super.lng);
        sendPositionField(device, id, 2, -super.alt);
    }

    private void sendPositionField(FcDevice device, int instanceID, int elementID, double value) {
        byte fieldData[] = ByteBuffer.allocate(4).putFloat((float) value).array();
        device.sendSettingsObject("Waypoints", instanceID, "Position", elementID, fieldData);
    }


    public void delete() {
        // TODO delete waypoints from device
    }
}
