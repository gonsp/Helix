package Helix.interpreter.controller;


import Helix.interpreter.Position;

public class Drone {

    public Position pos;
    public boolean isLanded;
    public double direction; //0 -> 359

    public Drone() {
        pos = new Position(0, 0, 0);
        isLanded = true;
        direction = 0;
    }
}
