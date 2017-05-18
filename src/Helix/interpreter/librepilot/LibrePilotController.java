package Helix.interpreter.librepilot;

import Helix.interpreter.DroneController;
import Helix.interpreter.librepilot.uavtalk.H;
import Helix.interpreter.librepilot.uavtalk.UAVTalkXMLObject;
import Helix.interpreter.librepilot.uavtalk.device.FcDevice;
import Helix.interpreter.librepilot.uavtalk.device.FcUsbDevice;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class LibrePilotController extends DroneController {


    public LibrePilotController() {
        try {
            Map<String, UAVTalkXMLObject> xmlObjects = loadUAVODefinitions("src/Helix/interpreter/librepilot/uavtalk/definitions");
            FcDevice device = new FcUsbDevice(xmlObjects);
            device.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, UAVTalkXMLObject> loadUAVODefinitions(String path) throws Exception {
        System.out.println("Starting to load XML definitions");
        Map<String, UAVTalkXMLObject> xmlObjects = new TreeMap<>();


        MessageDigest crypt = MessageDigest.getInstance("SHA-1");     //single files hash
        MessageDigest cumucrypt = MessageDigest.getInstance("SHA-1"); //cumulative hash
        cumucrypt.reset();

        File XMLFolder = new File(path);
        for(File file : XMLFolder.listFiles()) {
            byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            String content = new String(encoded);

            crypt.reset();
            crypt.update(content.getBytes());
            cumucrypt.update(H.bytesToHex(crypt.digest()).toLowerCase().getBytes());

            if (content.length() > 0) {
                UAVTalkXMLObject obj = new UAVTalkXMLObject(content);
                xmlObjects.put(obj.getName(), obj);
            }
        }

        System.out.println("SHA1: " + H.bytesToHex(cumucrypt.digest()).toLowerCase());
        return xmlObjects;
    }

    @Override
    public void moveTo() {

    }
}