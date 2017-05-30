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
    private double dir;

    public SimulationController(GPSPosition homeLocation, double direction) {
        dir = direction;
        posGPS = homeLocation;
        pathHistory = new ArrayList<>();
        pathHistory.add(posGPS);
    }

    @Override
    public GPSPosition getGPS() {
        return new GPSPosition(posGPS);
    }

    @Override
    public double getDirection() {
        return dir;
    }

    @Override
    public void sendTakeOff(double height) {
        GPSPosition pos = new GPSPosition(posGPS);
        pos.move(new Position(0, 0, height));
        sendMoveTo(pos);
    }

    @Override
    public void sendMoveTo(GPSPosition pos) {
        posGPS = pos;
        updatePath();
    }

    @Override
    public void sendDirection(double direction) {
        dir = direction;
    }

    @Override
    public void sendLand() {
        posGPS.alt = 0;
        updatePath();
        showPath();
    }

    public void addRealPath(ArrayList<GPSPosition> historyGPS) {
        REAL_PATH_TEMPLATE = REAL_PATH_TEMPLATE.replace("<-COORDINATES->", pathToString(historyGPS));
        KML_TEMPLATE = KML_TEMPLATE.replace("<-REAL-PATH->", REAL_PATH_TEMPLATE);
    }

    private void updatePath() {
        pathHistory.add(new GPSPosition(posGPS));
    }

    private String pathToString(ArrayList<GPSPosition> path) {
        String s = new String();
        for(Position pos : path) {
            if(!s.isEmpty()) {
                s += "\n                ";
            }
            s += pos.toString(true);
        }
        return s;
    }

    private void showPath() {
        String KML = KML_TEMPLATE.replace("<-COORDINATES->", pathToString(pathHistory));
        KML = KML.replace("<-REAL-PATH->", "");
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
                                       + "        <LineStyle>\n"
                                       + "            <color>7f00ffff</color>\n"
                                       + "            <width>4</width>\n"
                                       + "        </LineStyle>\n"
                                       + "        <PolyStyle>\n"
                                       + "            <color>7f00ff00</color>\n"
                                       + "        </PolyStyle>\n"
                                       + "    </Style>\n"
                                       + "    <Style id=\"real_path\">\n"
                                       + "        <LineStyle>\n"
                                       + "            <color>7fffff00</color>\n"
                                       + "            <width>3</width>\n"
                                       + "        </LineStyle>\n"
                                       + "        <PolyStyle>\n"
                                       + "            <color>7fff0000</color>\n"
                                       + "        </PolyStyle>\n"
                                       + "    </Style>\n"
                                       + "    <Placemark>\n"
                                       + "        <name>Path</name>\n"
                                       + "        <description>Drone path</description>\n"
                                       + "        <styleUrl>#path</styleUrl>\n"
                                       + "        <LineString>\n"
                                       + "            <extrude>1</extrude>\n"
                                       + "            <altitudeMode>absolute</altitudeMode>\n"
                                       + "            <coordinates>\n"
                                       + "                <-COORDINATES->\n"
                                       + "            </coordinates>\n"
                                       + "        </LineString>\n"
                                       + "    </Placemark>\n"
                                       + "    <-REAL-PATH->\n"
                                       + "</Document>\n"
                                       + "</kml>\n";

    private static String REAL_PATH_TEMPLATE = "    <Placemark>\n"
                                             + "        <name>Path</name>\n"
                                             + "        <description>Drone real path</description>\n"
                                             + "        <styleUrl>#real_path</styleUrl>\n"
                                             + "        <LineString>\n"
                                             + "            <extrude>1</extrude>\n"
                                             + "            <altitudeMode>absolute</altitudeMode>\n"
                                             + "            <coordinates>\n"
                                             + "                <-COORDINATES->\n"
                                             + "            </coordinates>\n"
                                             + "        </LineString>\n"
                                             + "    </Placemark>";
}
