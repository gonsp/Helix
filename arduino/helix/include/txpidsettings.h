/**
 ******************************************************************************
 * @addtogroup UAVObjects LibrePilot UAVObjects
 * @{
 * @addtogroup TxPIDSettings TxPIDSettings
 * @brief Settings used by @ref TxPID optional module to tune PID settings using R/C transmitter
 *
 * Autogenerated files and functions for TxPIDSettings Object
 *
 * @{
 *
 * @file       txpidsettings.h
 *
 * @author     The LibrePilot Project, https://www.librepilot.org, (C) 2017.
 *             The OpenPilot Team, http://www.openpilot.org Copyright (C) 2010-2013.
 *
 * @brief      Arduino Header of the TxPIDSettings object. This file has been
 *             automatically generated by the UAVObjectGenerator.
 *
 * @note       Object definition file: txpidsettings.xml.
 *             This is an automatically generated file.
 *             DO NOT modify manually.
 *
 * @see        The GNU Public License (GPL) Version 3
 *
 *****************************************************************************/
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

#ifndef TXPIDSETTINGS_H
#define TXPIDSETTINGS_H
#include <stdbool.h>
/* Object constants */
#define TXPIDSETTINGS_OBJID 0xF23A19C8
#define TXPIDSETTINGS_ISSINGLEINST 1
#define TXPIDSETTINGS_ISSETTINGS 1
#define TXPIDSETTINGS_ISPRIORITY 0
#define TXPIDSETTINGS_NUMBYTES sizeof(TxPIDSettingsData)

/* Field ThrottleRange information */

// Array element names for field ThrottleRange
typedef enum {
    TXPIDSETTINGS_THROTTLERANGE_MIN=0,
    TXPIDSETTINGS_THROTTLERANGE_MAX=1
} TxPIDSettingsThrottleRangeElem;

// Number of elements for field ThrottleRange
#define TXPIDSETTINGS_THROTTLERANGE_NUMELEM 2

/* Field MinPID information */

// Array element names for field MinPID
typedef enum {
    TXPIDSETTINGS_MINPID_INSTANCE1=0,
    TXPIDSETTINGS_MINPID_INSTANCE2=1,
    TXPIDSETTINGS_MINPID_INSTANCE3=2
} TxPIDSettingsMinPIDElem;

// Number of elements for field MinPID
#define TXPIDSETTINGS_MINPID_NUMELEM 3

/* Field MaxPID information */

// Array element names for field MaxPID
typedef enum {
    TXPIDSETTINGS_MAXPID_INSTANCE1=0,
    TXPIDSETTINGS_MAXPID_INSTANCE2=1,
    TXPIDSETTINGS_MAXPID_INSTANCE3=2
} TxPIDSettingsMaxPIDElem;

// Number of elements for field MaxPID
#define TXPIDSETTINGS_MAXPID_NUMELEM 3

/* Field EasyTunePitchRollRateFactors information */

// Array element names for field EasyTunePitchRollRateFactors
typedef enum {
    TXPIDSETTINGS_EASYTUNEPITCHROLLRATEFACTORS_I=0,
    TXPIDSETTINGS_EASYTUNEPITCHROLLRATEFACTORS_D=1
} TxPIDSettingsEasyTunePitchRollRateFactorsElem;

// Number of elements for field EasyTunePitchRollRateFactors
#define TXPIDSETTINGS_EASYTUNEPITCHROLLRATEFACTORS_NUMELEM 2

/* Field EasyTuneYawRateFactors information */

// Array element names for field EasyTuneYawRateFactors
typedef enum {
    TXPIDSETTINGS_EASYTUNEYAWRATEFACTORS_P=0,
    TXPIDSETTINGS_EASYTUNEYAWRATEFACTORS_I=1,
    TXPIDSETTINGS_EASYTUNEYAWRATEFACTORS_D=2
} TxPIDSettingsEasyTuneYawRateFactorsElem;

// Number of elements for field EasyTuneYawRateFactors
#define TXPIDSETTINGS_EASYTUNEYAWRATEFACTORS_NUMELEM 3

/* Field UpdateMode information */

// Enumeration options for field UpdateMode
typedef enum __attribute__ ((__packed__)) {
    TXPIDSETTINGS_UPDATEMODE_NEVER=0,
    TXPIDSETTINGS_UPDATEMODE_WHENARMED=1,
    TXPIDSETTINGS_UPDATEMODE_ALWAYS=2
} TxPIDSettingsUpdateModeOptions;

/* Field BankNumber information */

// Enumeration options for field BankNumber
typedef enum __attribute__ ((__packed__)) {
    TXPIDSETTINGS_BANKNUMBER_BANK1=0,
    TXPIDSETTINGS_BANKNUMBER_BANK2=1,
    TXPIDSETTINGS_BANKNUMBER_BANK3=2
} TxPIDSettingsBankNumberOptions;

/* Field Inputs information */

// Enumeration options for field Inputs
typedef enum __attribute__ ((__packed__)) {
    TXPIDSETTINGS_INPUTS_THROTTLE=0,
    TXPIDSETTINGS_INPUTS_ACCESSORY0=1,
    TXPIDSETTINGS_INPUTS_ACCESSORY1=2,
    TXPIDSETTINGS_INPUTS_ACCESSORY2=3,
    TXPIDSETTINGS_INPUTS_ACCESSORY3=4,
    TXPIDSETTINGS_INPUTS_ACCESSORY4=5,
    TXPIDSETTINGS_INPUTS_ACCESSORY5=6
} TxPIDSettingsInputsOptions;

// Array element names for field Inputs
typedef enum {
    TXPIDSETTINGS_INPUTS_INSTANCE1=0,
    TXPIDSETTINGS_INPUTS_INSTANCE2=1,
    TXPIDSETTINGS_INPUTS_INSTANCE3=2
} TxPIDSettingsInputsElem;

// Number of elements for field Inputs
#define TXPIDSETTINGS_INPUTS_NUMELEM 3

/* Field PIDs information */

// Enumeration options for field PIDs
typedef enum __attribute__ ((__packed__)) {
    TXPIDSETTINGS_PIDS_DISABLED=0,
    TXPIDSETTINGS_PIDS_EASYTUNERATEPITCH=1,
    TXPIDSETTINGS_PIDS_EASYTUNERATEROLL=2,
    TXPIDSETTINGS_PIDS_ROLLRATEKP=3,
    TXPIDSETTINGS_PIDS_ROLLRATEKI=4,
    TXPIDSETTINGS_PIDS_ROLLRATEKD=5,
    TXPIDSETTINGS_PIDS_ROLLRATEILIMIT=6,
    TXPIDSETTINGS_PIDS_ROLLRATERESP=7,
    TXPIDSETTINGS_PIDS_PITCHRATEKP=8,
    TXPIDSETTINGS_PIDS_PITCHRATEKI=9,
    TXPIDSETTINGS_PIDS_PITCHRATEKD=10,
    TXPIDSETTINGS_PIDS_PITCHRATEILIMIT=11,
    TXPIDSETTINGS_PIDS_PITCHRATERESP=12,
    TXPIDSETTINGS_PIDS_ROLLPITCHRATEKP=13,
    TXPIDSETTINGS_PIDS_ROLLPITCHRATEKI=14,
    TXPIDSETTINGS_PIDS_ROLLPITCHRATEKD=15,
    TXPIDSETTINGS_PIDS_ROLLPITCHRATEILIMIT=16,
    TXPIDSETTINGS_PIDS_ROLLPITCHRATERESP=17,
    TXPIDSETTINGS_PIDS_YAWRATEKP=18,
    TXPIDSETTINGS_PIDS_YAWRATEKI=19,
    TXPIDSETTINGS_PIDS_YAWRATEKD=20,
    TXPIDSETTINGS_PIDS_YAWRATEILIMIT=21,
    TXPIDSETTINGS_PIDS_YAWRATERESP=22,
    TXPIDSETTINGS_PIDS_ROLLATTITUDEKP=23,
    TXPIDSETTINGS_PIDS_ROLLATTITUDEKI=24,
    TXPIDSETTINGS_PIDS_ROLLATTITUDEILIMIT=25,
    TXPIDSETTINGS_PIDS_ROLLATTITUDERESP=26,
    TXPIDSETTINGS_PIDS_PITCHATTITUDEKP=27,
    TXPIDSETTINGS_PIDS_PITCHATTITUDEKI=28,
    TXPIDSETTINGS_PIDS_PITCHATTITUDEILIMIT=29,
    TXPIDSETTINGS_PIDS_PITCHATTITUDERESP=30,
    TXPIDSETTINGS_PIDS_ROLLPITCHATTITUDEKP=31,
    TXPIDSETTINGS_PIDS_ROLLPITCHATTITUDEKI=32,
    TXPIDSETTINGS_PIDS_ROLLPITCHATTITUDEILIMIT=33,
    TXPIDSETTINGS_PIDS_ROLLPITCHATTITUDERESP=34,
    TXPIDSETTINGS_PIDS_YAWATTITUDEKP=35,
    TXPIDSETTINGS_PIDS_YAWATTITUDEKI=36,
    TXPIDSETTINGS_PIDS_YAWATTITUDEILIMIT=37,
    TXPIDSETTINGS_PIDS_YAWATTITUDERESP=38,
    TXPIDSETTINGS_PIDS_ROLLEXPO=39,
    TXPIDSETTINGS_PIDS_PITCHEXPO=40,
    TXPIDSETTINGS_PIDS_ROLLPITCHEXPO=41,
    TXPIDSETTINGS_PIDS_YAWEXPO=42,
    TXPIDSETTINGS_PIDS_GYROTAU=43,
    TXPIDSETTINGS_PIDS_ACROROLLFACTOR=44,
    TXPIDSETTINGS_PIDS_ACROPITCHFACTOR=45,
    TXPIDSETTINGS_PIDS_ACROROLLPITCHFACTOR=46,
    TXPIDSETTINGS_PIDS_ALTITUDEPOSKP=47,
    TXPIDSETTINGS_PIDS_ALTITUDEVELOCITYKP=48,
    TXPIDSETTINGS_PIDS_ALTITUDEVELOCITYKI=49,
    TXPIDSETTINGS_PIDS_ALTITUDEVELOCITYKD=50,
    TXPIDSETTINGS_PIDS_ALTITUDEVELOCITYBETA=51,
    TXPIDSETTINGS_PIDS_ACCELTAU=52,
    TXPIDSETTINGS_PIDS_ACCELKP=53,
    TXPIDSETTINGS_PIDS_ACCELKI=54
} TxPIDSettingsPIDsOptions;

// Array element names for field PIDs
typedef enum {
    TXPIDSETTINGS_PIDS_INSTANCE1=0,
    TXPIDSETTINGS_PIDS_INSTANCE2=1,
    TXPIDSETTINGS_PIDS_INSTANCE3=2
} TxPIDSettingsPIDsElem;

// Number of elements for field PIDs
#define TXPIDSETTINGS_PIDS_NUMELEM 3

/* Field EasyTuneRatePIDRecalculateYaw information */

// Enumeration options for field EasyTuneRatePIDRecalculateYaw
typedef enum __attribute__ ((__packed__)) {
    TXPIDSETTINGS_EASYTUNERATEPIDRECALCULATEYAW_FALSE=0,
    TXPIDSETTINGS_EASYTUNERATEPIDRECALCULATEYAW_TRUE=1
} TxPIDSettingsEasyTuneRatePIDRecalculateYawOptions;



typedef struct __attribute__ ((__packed__)) {
    float Min;
    float Max;
}  TxPIDSettingsThrottleRangeData ;
typedef struct __attribute__ ((__packed__)) {
    float array[2];
}  TxPIDSettingsThrottleRangeDataArray ;
#define TxPIDSettingsThrottleRangeToArray( var ) UAVObjectFieldToArray( TxPIDSettingsThrottleRangeData, var )

typedef struct __attribute__ ((__packed__)) {
    float Instance1;
    float Instance2;
    float Instance3;
}  TxPIDSettingsMinPIDData ;
typedef struct __attribute__ ((__packed__)) {
    float array[3];
}  TxPIDSettingsMinPIDDataArray ;
#define TxPIDSettingsMinPIDToArray( var ) UAVObjectFieldToArray( TxPIDSettingsMinPIDData, var )

typedef struct __attribute__ ((__packed__)) {
    float Instance1;
    float Instance2;
    float Instance3;
}  TxPIDSettingsMaxPIDData ;
typedef struct __attribute__ ((__packed__)) {
    float array[3];
}  TxPIDSettingsMaxPIDDataArray ;
#define TxPIDSettingsMaxPIDToArray( var ) UAVObjectFieldToArray( TxPIDSettingsMaxPIDData, var )

typedef struct __attribute__ ((__packed__)) {
    float I;
    float D;
}  TxPIDSettingsEasyTunePitchRollRateFactorsData ;
typedef struct __attribute__ ((__packed__)) {
    float array[2];
}  TxPIDSettingsEasyTunePitchRollRateFactorsDataArray ;
#define TxPIDSettingsEasyTunePitchRollRateFactorsToArray( var ) UAVObjectFieldToArray( TxPIDSettingsEasyTunePitchRollRateFactorsData, var )

typedef struct __attribute__ ((__packed__)) {
    float P;
    float I;
    float D;
}  TxPIDSettingsEasyTuneYawRateFactorsData ;
typedef struct __attribute__ ((__packed__)) {
    float array[3];
}  TxPIDSettingsEasyTuneYawRateFactorsDataArray ;
#define TxPIDSettingsEasyTuneYawRateFactorsToArray( var ) UAVObjectFieldToArray( TxPIDSettingsEasyTuneYawRateFactorsData, var )

typedef struct __attribute__ ((__packed__)) {
    TxPIDSettingsInputsOptions Instance1;
    TxPIDSettingsInputsOptions Instance2;
    TxPIDSettingsInputsOptions Instance3;
}  TxPIDSettingsInputsData ;
typedef struct __attribute__ ((__packed__)) {
    TxPIDSettingsInputsOptions array[3];
}  TxPIDSettingsInputsDataArray ;
#define TxPIDSettingsInputsToArray( var ) UAVObjectFieldToArray( TxPIDSettingsInputsData, var )

typedef struct __attribute__ ((__packed__)) {
    TxPIDSettingsPIDsOptions Instance1;
    TxPIDSettingsPIDsOptions Instance2;
    TxPIDSettingsPIDsOptions Instance3;
}  TxPIDSettingsPIDsData ;
typedef struct __attribute__ ((__packed__)) {
    TxPIDSettingsPIDsOptions array[3];
}  TxPIDSettingsPIDsDataArray ;
#define TxPIDSettingsPIDsToArray( var ) UAVObjectFieldToArray( TxPIDSettingsPIDsData, var )


/*
 * Packed Object data (unaligned).
 * Should only be used where 4 byte alignment can be guaranteed
 * (eg a single instance on the heap)
 */
typedef struct {
    TxPIDSettingsThrottleRangeData ThrottleRange;
    TxPIDSettingsMinPIDData MinPID;
    TxPIDSettingsMaxPIDData MaxPID;
    TxPIDSettingsEasyTunePitchRollRateFactorsData EasyTunePitchRollRateFactors;
    TxPIDSettingsEasyTuneYawRateFactorsData EasyTuneYawRateFactors;
    TxPIDSettingsUpdateModeOptions UpdateMode;
    TxPIDSettingsBankNumberOptions BankNumber;
    TxPIDSettingsInputsData Inputs;
    TxPIDSettingsPIDsData PIDs;
    TxPIDSettingsEasyTuneRatePIDRecalculateYawOptions EasyTuneRatePIDRecalculateYaw;

} __attribute__((packed)) TxPIDSettingsDataPacked;

/*
 * Packed Object data.
 * Alignment is forced to 4 bytes
 */
typedef TxPIDSettingsDataPacked __attribute__((aligned(4))) TxPIDSettingsData;

/*
 * Union to apply the data array to and to use as structured object data
 */
union {
    TxPIDSettingsDataPacked data;
    byte arr[TXPIDSETTINGS_NUMBYTES];
 } TxPIDSettingsDataUnion;

#endif // TXPIDSETTINGS_H

/**
 * @}
 * @}
 */