package Helix.interpreter;

import Helix.interpreter.librepilot.LibrePilotController;

public class Interpreter {

    private DroneController droneController;

    public Interpreter() {
        droneController = new LibrePilotController();
    }
}