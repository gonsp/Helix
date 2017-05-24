package Helix.interpreter.controller.simulation;

import Helix.interpreter.controller.DroneController;
import Helix.interpreter.GPSPosition;
import Helix.interpreter.Position;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SimulationController extends DroneController {

    private ArrayList<GPSPosition> pathHistory;
    private GPSPosition posGPS;

    public SimulationController(GPSPosition homeLocation) {
        super(homeLocation);
        posGPS = homeLocation;
        pathHistory = new ArrayList<>();
        pathHistory.add(posGPS);
    }

    @Override
    public GPSPosition getGPS() {
        return new GPSPosition(posGPS);
    }

    @Override
    protected void sendMoveTo(GPSPosition pos) {
        posGPS = pos;
        updatePath();
    }

    @Override
    protected void sendLand() {
        posGPS.alt = 0;
        updatePath();
    }

    private void updatePath() {
        pathHistory.add(new GPSPosition(posGPS));
        showPath();
    }

    private void showPath() {
        String s = new String();
        for(Position pos : pathHistory) {
            if(!s.isEmpty()) {
                s += "\n            ";
            }
            s += pos.toString(true);
        }
        String KML = KML_TEMPLATE.replace("<-COORDINATES->", s);
        try {
            PrintWriter out = new PrintWriter("path_simulation.kml");
            out.print(KML);
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                File file = new File("path_simulation.kml");
                desktop.browse(file.toURI());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String KML_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                       + "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
                                       + "<Document>\n"
                                       + "    <name>Drone path</name>\n"
                                       + "    <description>Simulated flight of a drone using the hardware-abstract high-level drone programming language \"Helix\"</description>+\n"
                                       + "    <Style id=\"path\">\n"
                                       + "    <LineStyle>\n"
                                       + "        <color>7f00ffff</color>\n"
                                       + "        <width>4</width>\n"
                                       + "    </LineStyle>\n"
                                       + "    <PolyStyle>\n"
                                       + "        <color>7f00ff00</color>\n"
                                       + "    </PolyStyle>\n"
                                       + "    </Style>\n"
                                       + "    <Placemark>\n"
                                       + "    <name>Path</name>\n"
                                       + "    <description>Drone path</description>\n"
                                       + "    <styleUrl>#path</styleUrl>\n"
                                       + "    <LineString>\n"
                                       + "        <extrude>1</extrude>\n"
                                       + "        <altitudeMode>relativeToGround</altitudeMode>\n"
                                       + "        <coordinates>\n"
                                       + "            <-COORDINATES->\n"
                                       + "        </coordinates>\n"
                                       + "    </LineString>\n"
                                       + "    </Placemark>\n"
                                       + "</Document>\n"
                                       + "</kml>\n";
}
