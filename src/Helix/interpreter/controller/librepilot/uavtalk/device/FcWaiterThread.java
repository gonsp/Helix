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

package Helix.interpreter.controller.librepilot.uavtalk.device;


import Helix.interpreter.controller.librepilot.uavtalk.Utils;
import Helix.interpreter.controller.librepilot.uavtalk.UAVTalkObject;

public abstract class FcWaiterThread extends Thread {
    public final static byte MASK_TIMESTAMP = (byte) 0x80;
    final FcDevice mDevice;

    FcWaiterThread(FcDevice device) {
        this.mDevice = device;
    }

    protected abstract void stopThread();

    final boolean handleMessageType(byte msgType, UAVTalkObject obj) {
        switch (msgType) {
            case (byte) 0x20:
            case (byte) 0xa0:
                //handle default package, nothing to do
                break;
            case (byte) 0x21:
            case (byte) 0xa1:
                //handle request message, nobody should request from LP2Go (so we don't implement this)
                System.out.println("UAVTalk: " + "Received Object Request, but won't send any " + obj.getId());
                break;
            case (byte) 0x22:
            case (byte) 0xa2:
                //handle object with ACK REQ, means send ACK
                mDevice.sendAck(obj.getId(), 0);
                System.out.println("UAVTalk: " + "Received Object with ACK Request " + obj.getId());
                break;
            case (byte) 0x23:
            case (byte) 0xa3:
                //handle received ACK, e.g. save in Object that it has been acknowledged
                System.out.println("UAVTalk: " + "Received ACK Object " + obj.getId());
                break;
            case (byte) 0x24:
            case (byte) 0xa4:
                //handle NACK, show warning and add to request blacklist
                mDevice.nackedObjects.add(obj.getId());
                //mDevice.mActivity.incRxObjectsBad();
                System.out.println("UAVTalk: " + "Received NACK Object " + obj.getId());
                break;
            default:
                //mDevice.mActivity.incRxObjectsBad();
                byte[] b = new byte[1];
                b[0] = msgType;
                System.out.println("UAVTalk: " + "Received bad Object Type " + Utils.bytesToHex(b));
                return false;
        }
        return true;
    }
}
