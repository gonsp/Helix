package Helix.interpreter.controller.librepilot;

import Helix.interpreter.GPSPosition;

public interface GPSListener {

    void onGPSUpdate(GPSPosition newPos);
}
