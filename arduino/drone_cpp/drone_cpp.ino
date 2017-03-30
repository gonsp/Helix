#include "include/flightstatus.h"
#include "include/accelsensor.h"
#include "LibrePilotSerial.h"
#include <SoftwareSerial.h>
 
SoftwareSerial serial(2, 3);  // RX, TX
LibrePilotSerial droneSerial(&serial);
 
void setup() {
  droneSerial.serial->begin(57600);
  Serial.begin(9600);
}
 
void loop() {
  droneSerial.request(FLIGHTSTATUS_OBJID);
  //Receive object from FC. This function will block until the specified object was received or it times out.
  //It returns true if a valid packet was received
  //the packet is stored in the array of the object packet union
  boolean ok = droneSerial.receive(FLIGHTSTATUS_OBJID, FlightStatusDataUnion.arr, 200);
  if(ok) {
    Serial.print("Flight status: ");
    Serial.println(FlightStatusDataUnion.data.Armed);
    analogWrite(5, 255);
  } else {
    //Serial.println("Error reading flight status");
    analogWrite(5, 0);
  }

  delay(200);
  
  droneSerial.request(ACCELSENSOR_OBJID);
  ok = droneSerial.receive(ACCELSENSOR_OBJID, AccelSensorDataUnion.arr, 200);
  if(ok) {
    Serial.print("Accel X: ");
    Serial.println(AccelSensorDataUnion.data.x);
    analogWrite(5, 255);
  } else {
    //Serial.println("Error reading accelerator info");
    analogWrite(5, 0);
  }
  
  delay(200);
}
