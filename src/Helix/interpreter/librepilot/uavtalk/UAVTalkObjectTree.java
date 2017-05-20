/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package Helix.interpreter.librepilot.uavtalk;

import Helix.interpreter.librepilot.LibrePilotController;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UAVTalkObjectTree {

    private static final String XML_PATH = "resources/definitions/";

    private final ConcurrentHashMap<String, UAVTalkObject> objects;
    private Map<String, UAVTalkXMLObject> xmlObjects;

    public UAVTalkObjectTree() {
        objects = new ConcurrentHashMap<>();
        xmlObjects = new TreeMap<>();

        try {
            loadUAVODefinitions();
        } catch (Exception e) {
            System.out.println("ERROR READING UAVO DEFINITIONS");
            e.printStackTrace();
        }

        for(UAVTalkXMLObject xmlObject : xmlObjects.values()) {
            objects.put(xmlObject.getId(), new UAVTalkObject(xmlObject));
        }
    }

    public Map<String, UAVTalkXMLObject> getXmlObjects() {
        return xmlObjects;
    }

    public void setListener(String objName, UAVTalkObjectListener listener) {
        String id = xmlObjects.get(objName).getId();
        System.out.println("HELLOOOOOOOOOOOO = " + id);
        objects.get(id).setListener(listener);
    }

    public UAVTalkObjectListener getListener(String objName) {
        if (objects.get(xmlObjects.get(objName)) != null) {
            return objects.get(xmlObjects.get(objName).getId()).getListener();
        } else {
            return null;
        }
    }

    public int size() {
        return objects.size();
    }

    public void removeListener(String objName) {
        objects.get(xmlObjects.get(objName).getId()).removeListener();
    }

    public String toString() {
        String ret = "";
        for (String key : xmlObjects.keySet()) {
            ret += xmlObjects.get(key).getId() + " " + key + "\r\n";
        }
        return ret;
    }

    public UAVTalkObject getObjectNoCreate(String name) {
        return objects.get(xmlObjects.get(name).getId());
    }

    public UAVTalkObject getObjectFromID(String oID) {
        UAVTalkObject obj = objects.get(oID);
        if (obj == null) {
            for(UAVTalkXMLObject xmlObject : xmlObjects.values()) {
                if(xmlObject.getId().equals(oID)) {
                    obj = new UAVTalkObject(xmlObject);
                }
            }
        }
        return obj;
    }

    public UAVTalkObject getObjectFromName(String name) {
        UAVTalkXMLObject xmlObject = xmlObjects.get(name);
        String oID = xmlObject.getId();
        UAVTalkObject obj = objects.get(oID);
        if (obj == null) {
            System.out.println("OFN: " + "CREATED NEW OBJECT! " + name + " " + oID);
            obj = new UAVTalkObject(xmlObject);
        }
        return obj;
    }

    public void updateObject(UAVTalkObject obj) {
        try {
            if (objects.get(obj.getId()) == null ||
                    !objects.get(obj.getId()).isWriteBlocked()) { //FIXME: This is maybe expensive
                objects.put(obj.getId(), obj);
            }
        } catch (NullPointerException e) {
            System.out.println("WAR: " + "objects not initialized");
        }
    }

    public int getElementIndex(String objectname, String fieldname, String element) {
        int retval;
        UAVTalkXMLObject xmlobj = xmlObjects.get(objectname);
        UAVTalkXMLObject.UAVTalkXMLObjectField xmlfield = xmlobj.getFields().get(fieldname);


        retval = xmlfield.mElements.indexOf(element);

        //VisualLog.d(xmlobj.getId(), Arrays.toString(xmlfield.getElements().toArray()) + "~" +element+"#" +retval);

        // -1 is the return value if indexOf does not return anything
        // that means there is only the default element
        if (retval < 0) {
            retval = 0;
        }

        return retval;
    }

    private Map<String, UAVTalkXMLObject> loadUAVODefinitions() throws Exception {
        System.out.println("Starting to load XML definitions");

        MessageDigest crypt = MessageDigest.getInstance("SHA-1");     //single files hash
        MessageDigest cumucrypt = MessageDigest.getInstance("SHA-1"); //cumulative hash
        cumucrypt.reset();

        CodeSource src = LibrePilotController.class.getProtectionDomain().getCodeSource();
        if(src != null) {
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            ZipEntry e = zip.getNextEntry();
            while(e != null) {
                String name = e.getName();
                if(name.startsWith(XML_PATH) && !name.equals(XML_PATH)) {
                    File file = new File(name);
                    byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                    String content = new String(encoded);

                    crypt.reset();
                    crypt.update(content.getBytes());
                    cumucrypt.update(Utils.bytesToHex(crypt.digest()).toLowerCase().getBytes());

                    if (content.length() > 0) {
                        UAVTalkXMLObject obj = new UAVTalkXMLObject(content);
                        xmlObjects.put(obj.getName(), obj);
                    }
                }
                e = zip.getNextEntry();
            }
        }

        System.out.println("SHA1: " + Utils.bytesToHex(cumucrypt.digest()).toLowerCase());
        return xmlObjects;
    }
}
