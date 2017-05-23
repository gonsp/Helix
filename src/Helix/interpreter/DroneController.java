package Helix.interpreter;


public abstract class DroneController {

    private Drone drone;

    protected DroneController() {
        drone = new Drone();
    }

    public abstract void moveTo();
    public abstract void land();
}
