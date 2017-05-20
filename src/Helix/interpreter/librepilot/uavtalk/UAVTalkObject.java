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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

public class UAVTalkObject {

    private final String mId;
    private final HashMap<Integer, UAVTalkObjectInstance> mInstances;
    private UAVTalkObjectListener mListener = null;
    private boolean mWriteBlocked = false;

    private UAVTalkMetaData mMetaData = null;
    private UAVTalkXMLObject xmlObject;

    public UAVTalkObject(UAVTalkXMLObject xmlObject) {
        this.mId = xmlObject.getId();
        this.xmlObject = xmlObject;
        mInstances = new HashMap<>();
    }

    public static byte[] getReqMsg(byte type, String objectId, int instance) {
        byte[] retval = new byte[11];

        retval[0] = 0x3c;
        retval[1] = type;

        retval[2] = 0x0a;
        retval[3] = 0x00;

        byte[] objId = Utils.hexStringToByteArray(objectId);

        retval[4] = objId[3];
        retval[5] = objId[2];
        retval[6] = objId[1];
        retval[7] = objId[0];

        byte[] iid = Utils.toBytes(instance);

        retval[8] = iid[3];
        retval[9] = iid[2];

        retval[10] = (byte) (Utils.crc8(retval, 0, 10) & 0xff);

        return retval;
    }

    public String getId() {
        return mId;
    }

    public HashMap<Integer, UAVTalkObjectInstance> getInstances() {
        return mInstances;
    }

    protected UAVTalkObjectListener getListener() {
        return mListener;
    }

    protected void setListener(UAVTalkObjectListener l) {
        if (mListener == null) {
            this.mListener = l;
        } else {
            throw new IllegalStateException("Listener already set");
        }
    }

    public boolean isWriteBlocked() {
        return mWriteBlocked;
    }

    public void setWriteBlocked(boolean mWriteBlocked) {
        this.mWriteBlocked = mWriteBlocked;
    }

    public void setInstance(UAVTalkObjectInstance instance) {
        mInstances.put(instance.getId(), instance);
        if (getListener() != null) {
            getListener().onObjectUpdate(this);
        }
    }

    protected void removeListener() {
        mListener = null;
    }

    public String toString() {
        return mId + " " + Utils.bytesToHex(getInstance(0).getData());
    }

    public UAVTalkObjectInstance getInstance(int id) {
        return mInstances.get(id);
    }

    public byte[] toMessage(byte type, int instance, boolean asAck) {
        UAVTalkObjectInstance inst = mInstances.get(instance);

        byte[] instData = null;

        if (inst != null) {
            instData = inst.getData();
        }

        if (instData == null) {
            return new byte[0];
        }

        byte[] retval;

        if (asAck) {
            retval = new byte[11]; //only the header and CRC
        } else {
            retval = new byte[instData.length + 11]; // data as well
            System.arraycopy(instData, 0, retval, 10, instData.length); // copy the data
        }

        retval[0] = 0x3c;
        retval[1] = type;

        byte[] len = Utils.toBytes(instData.length + 10);
        retval[2] = len[3];
        retval[3] = len[2];

        byte[] objId = Utils.hexStringToByteArray(this.mId);

        retval[4] = objId[3];
        retval[5] = objId[2];
        retval[6] = objId[1];
        retval[7] = objId[0];

        byte[] iid = Utils.toBytes(instance);

        retval[8] = iid[3];
        retval[9] = iid[2];

        retval[retval.length - 1] = (byte) (Utils.crc8(retval, 0, retval.length - 1) & 0xff);

        return retval;
    }

    public Object getData(String objectname, String fieldname)
            throws UAVTalkMissingObjectException {
        return getData(objectname, 0, fieldname, 0);
    }


/*
    public Object getData(String objectname, String fieldname, String element)
            throws UAVTalkMissingObjectException {
        return getData(objectname, 0, fieldname, element);
    }


    public Object getData(String objectname, int instance, String fieldname, String elementname)
            throws UAVTalkMissingObjectException {
        return getData(objectname, instance, fieldname,
                getElementIndex(objectname, fieldname, elementname));
    }
*/


    public Object getData(String objectname, int instance, String fieldname, int element)
            throws UAVTalkMissingObjectException {

        UAVTalkXMLObject.UAVTalkXMLObjectField xmlfield = xmlObject.getFields().get(fieldname);

        UAVTalkObjectInstance ins = getInstance(instance);
        if (ins == null) {
            UAVTalkMissingObjectException e = new UAVTalkMissingObjectException(objectname+"."+instance+"."+fieldname+"."+element);
            e.setInstance(instance);
            e.setObjectname(objectname);
            e.setIsSettings(xmlObject.isSettings());
            throw e;
        }

        byte[] data = ins.getData();

        int pos = xmlfield.mPos;

        Object retval = null;
        if (data != null) {
            switch (xmlfield.mType) {
                case (UAVTalkXMLObject.FIELDTYPE_ENUM): {
                    byte[] fielddata = new byte[1];
                    byte b = data[pos + element];
                    fielddata[0] = b;
                    try {
                        retval = xmlfield.mOptions[Utils.toInt(b)];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("AIOOBE: " + Utils.toInt(b) + " " + data.length + " " + b + " " +
                                Utils.bytesToHex(fielddata) + " " + Utils.bytesToHex(data) + " " + pos +
                                " " + element);
                    }
                    break;
                }
                case (UAVTalkXMLObject.FIELDTYPE_FLOAT32): {
                    byte[] fielddata = new byte[4];
                    System.arraycopy(data, pos + element * 4, fielddata, 0, 4);
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    float f = ByteBuffer.wrap(fielddata).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    retval = f;
                    break;
                }
                case (UAVTalkXMLObject.FIELDTYPE_UINT32): {
                    byte[] fielddata = new byte[8]; //we need 8 bytes to get a long value
                    System.arraycopy(data, pos + element * 4, fielddata, 0, 4);
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    long l = ByteBuffer.wrap(fielddata).order(
                            //set 4 higer bytes to zero (truncates sign)
                            ByteOrder.LITTLE_ENDIAN).getLong() & 0xffffff;
                    retval = l;
                    break;
                }
                case (UAVTalkXMLObject.FIELDTYPE_INT32): {
                    byte[] fielddata = new byte[4];
                    System.arraycopy(data, pos, fielddata, 0, 4);
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    int i = ByteBuffer.wrap(fielddata).order(ByteOrder.LITTLE_ENDIAN).getInt();
                    retval = i;
                    break;
                }
                case (UAVTalkXMLObject.FIELDTYPE_UINT16): {
                    byte[] fielddata = new byte[4]; //we need four bytes to get an integer value
                    System.arraycopy(data, pos + element * 2, fielddata, 0, 2);
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    int i = ByteBuffer.wrap(fielddata).order(
                            //set 2 higher bytes to zero (truncates sign)
                            ByteOrder.LITTLE_ENDIAN).getInt() & 0xffff;
                    retval = i;
                    break;
                }
                case (UAVTalkXMLObject.FIELDTYPE_INT16): {
                    byte[] fielddata = new byte[2];
                    System.arraycopy(data, pos + element * 2, fielddata, 0, 2);
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    int i = ByteBuffer.wrap(fielddata).order(ByteOrder.LITTLE_ENDIAN).getShort();
                    retval = i;
                    break;
                }
                case (UAVTalkXMLObject.FIELDTYPE_UINT8): {
                    byte[] fielddata = new byte[1];
                    System.arraycopy(data, pos + element, fielddata, 0, 1);
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    int i = fielddata[0] & 0xff;
                    retval = i;
                    break;
                }
                case (UAVTalkXMLObject.FIELDTYPE_INT8): {
                    byte[] fielddata = new byte[1];
                    System.arraycopy(data, pos + element, fielddata, 0, 1);
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    int i = fielddata[0];
                    retval = i;
                    break;
                }
                default: {
                    retval = "Type not implemented";
                    break;
                }
            }
        }
        return retval;
    }
}
