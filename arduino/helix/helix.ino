#include "include/flightstatus.h"
#include "include/ManualControlCommand.h"
#include "include/gyrostate.h"
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

#define RCStatusReq MANUALCONTROLCOMMAND_OBJID, ManualControlCommandDataUnion.arr
#define RCStatus ManualControlCommandDataUnion.data

#define GyroStatusReq GYROSTATE_OBJID, GyroStateDataUnion.arr
#define GyroStatus GyroStateDataUnion.data
 
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
      ++i;
      droneSerial.request(id);
    }
    return true;
}
 
void setup() {
  droneSerial.serial->begin(57600);
  Serial.begin(9600);
  
  pinMode(PROX_TRIG, OUTPUT);
  pinMode(PROX_ECHO, INPUT);

  pinMode(13, OUTPUT);
}


void loop() {
  get_data(FlightStatusReq, true);
  if(FlightStatus.Armed == FLIGHTSTATUS_ARMED_ARMED && FlightStatus.FlightMode == FLIGHTSTATUS_FLIGHTMODE_STABILIZED2) {
    for(int i = 0; i < 100; ++i) {
      long height = get_prox();
      if(height <= MIN_HEIGHT) {
        Serial.println("Emergency! Fliying too low!");
        delay(50);
      } else {
        break;
      }
    }
  }
  delay(100);
}

