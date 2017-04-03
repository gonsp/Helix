#include "include/flightstatus.h"
#include "include/accelsensor.h"
#include "include/flightbatterystate.h"
#include "include/ManualControlSettings.h"
#include "LibrePilotSerial.h"
#include <SoftwareSerial.h>

#define TIMEOUT 100
#define WAIT_ATTEMPT 100
#define MAX_ATTEMPTS 10

#define PROX_TRIG 13
#define PROX_ECHO 12
#define MIN_HEIGHT 20

#define FlightStatusReq FLIGHTSTATUS_OBJID, FlightStatusDataUnion.arr
#define FlightStatus FlightStatusDataUnion.data

#define RCStatusReq MANUALCONTROLSETTINGS_OBJID, ManualControlSettingsDataUnion.arr
#define RCStatus ManualControlSettingsDataUnion.data
 
SoftwareSerial serial(2, 3);  // RX, TX
LibrePilotSerial droneSerial(&serial);

long get_prox() {
  long duration, distance;
  digitalWrite(PROX_TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(PROX_TRIG, HIGH);
  delayMicroseconds(10);
  digitalWrite(PROX_TRIG, LOW);
  duration = pulseIn(PROX_ECHO, HIGH);
  distance = (duration/2) / 29.1;
  return distance;  
}

void get_data(unsigned long id, byte* data, bool block) {
    droneSerial.request(id);
    int i = 0;
    while(!droneSerial.receive(id, data, TIMEOUT)) {
      if(!block && i >= MAX_ATTEMPTS) {
        return false;
      }
      delay(WAIT_ATTEMPT);
    }
    return true;
}
 
void setup() {
  droneSerial.serial->begin(115200);
  Serial.begin(9600);
  
  pinMode(PROX_TRIG, OUTPUT);
  pinMode(PROX_ECHO, INPUT);

  pinMode(13, OUTPUT);
}

void loop() {

  get_data(RCStatusReq, true);
  if(RCStatus.FlightModeNumber == 3) {
    long height = get_prox();
    if(height <= MIN_HEIGHT) {
      Serial.println("Emergency! Fliying too low!");
    }
  }

  delay(100);

  /*
  //Receive object from FC. This function will block until the specified object was received or it times out.
  //It returns true if a valid packet was received
  //the packet is stored in the array of the object packet union
  boolean ok = droneSerial.receive(FLIGHTSTATUS_OBJID, FlightStatusDataUnion.arr, TIMEOUT);
  if(ok) {
    Serial.print("Flight status: ");
    Serial.println(FlightStatusDataUnion.data.Armed);
    digitalWrite(13, LOW);
  } else {
    digitalWrite(13, HIGH);
    Serial.println("Error reading flight status");
  }

  delay(TIMEOUT);
}

/*
void loop() {
  droneSerial.request(FLIGHTSTATUS_OBJID);
  //Receive object from FC. This function will block until the specified object was received or it times out.
  //It returns true if a valid packet was received
  //the packet is stored in the array of the object packet union
  boolean ok = droneSerial.receive(FLIGHTSTATUS_OBJID, FlightStatusDataUnion.arr, TIMEOUT);
  if(ok) {
    Serial.print("Flight status: ");
    Serial.println(FlightStatusDataUnion.data.Armed);
    digitalWrite(13, LOW);
  } else {
    digitalWrite(13, HIGH);
    Serial.println("Error reading flight status");
  }

  delay(TIMEOUT);
  
  droneSerial.request(ACCELSENSOR_OBJID);
  ok = droneSerial.receive(ACCELSENSOR_OBJID, AccelSensorDataUnion.arr, TIMEOUT);
  if(ok) {
    Serial.print("Accel X: ");
    Serial.println(AccelSensorDataUnion.data.x);
    digitalWrite(13, LOW);
  } else {
    digitalWrite(13, HIGH);
    Serial.println("Error reading accelerator info");
  }

  delay(TIMEOUT);
  
  droneSerial.request(FLIGHTBATTERYSTATE_OBJID);
  ok = droneSerial.receive(FLIGHTBATTERYSTATE_OBJID, FlightBatteryStateDataUnion.arr, TIMEOUT);
  if(ok) {
    Serial.print("Battery voltage: ");
    Serial.println(FlightBatteryStateDataUnion.data.Voltage);
    Serial.println(FlightBatteryStateDataUnion.data.Current);
    Serial.println(FlightBatteryStateDataUnion.data.ConsumedEnergy);
    Serial.println(FlightBatteryStateDataUnion.data.EstimatedFlightTime);
    Serial.println(FlightBatteryStateDataUnion.data.NbCells);
    digitalWrite(13, LOW);
  } else {
    digitalWrite(13, HIGH);
    Serial.println("Error reading accelerator info");
  }
  
  delay(TIMEOUT);
  */
}

