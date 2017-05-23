package Helix.interpreter.simulation;

import Helix.interpreter.DroneController;
import Helix.Position;
import java.util.ArrayList;

public class SimulationController extends DroneController implements PathPlannerListener {

    private ArrayList<Position> pathHistory;

    public SimulationController() {
        pathHistory = new ArrayList<>();
        
        // Testing
        pathHistory.add(new Position(-112.2550785337791, 36.07954952145647,  2357));
        pathHistory.add(new Position(-112.2564540158376, 36.08395660588506,  2357));
        pathHistory.add(new Position(-112.2608216347552, 36.08612634548589,  2357));
        pathHistory.add(new Position(-112.2644963846444, 36.08627897945274,  2350));
        pathHistory.add(new Position(-112.2656969554589, 36.08649599090644,  2340));
    }

    @Override
    public void moveTo(Position pos) {
        pathHistory.add(pos);
    }
    
    @Override
    public void land() {
        String s = new String();
        for(Position pos : pathHistory) {
            s += pos.toString();
        }
        KML_TEMPLATE.replace("<-COORDINATES->", s);
        System.out.println(KML_TEMPLATE);
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
                                       + "        <altitudeMode>absolute</altitudeMode>\n"
                                       + "        <coordinates>\n"
                                       + "            <-COORDINATES->\n"
                                       + "        </coordinates>\n"
                                       + "    </LineString>\n"
                                       + "    </Placemark>\n"
                                       + "</Document>\n"
                                       + "</kml>\n";
}
