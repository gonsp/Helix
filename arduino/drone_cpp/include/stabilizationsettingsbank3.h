/**
 ******************************************************************************
 * @addtogroup UAVObjects LibrePilot UAVObjects
 * @{
 * @addtogroup StabilizationSettingsBank3 StabilizationSettingsBank3
 * @brief Currently selected PID bank
 *
 * Autogenerated files and functions for StabilizationSettingsBank3 Object
 *
 * @{
 *
 * @file       stabilizationsettingsbank3.h
 *
 * @author     The LibrePilot Project, https://www.librepilot.org, (C) 2017.
 *             The OpenPilot Team, http://www.openpilot.org Copyright (C) 2010-2013.
 *
 * @brief      Arduino Header of the StabilizationSettingsBank3 object. This file has been
 *             automatically generated by the UAVObjectGenerator.
 *
 * @note       Object definition file: stabilizationsettingsbank3.xml.
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

#ifndef STABILIZATIONSETTINGSBANK3_H
#define STABILIZATIONSETTINGSBANK3_H
#include <stdbool.h>
/* Object constants */
#define STABILIZATIONSETTINGSBANK3_OBJID 0x5C80D844
#define STABILIZATIONSETTINGSBANK3_ISSINGLEINST 1
#define STABILIZATIONSETTINGSBANK3_ISSETTINGS 1
#define STABILIZATIONSETTINGSBANK3_ISPRIORITY 0
#define STABILIZATIONSETTINGSBANK3_NUMBYTES sizeof(StabilizationSettingsBank3Data)

/* Field RollRatePID information */

// Array element names for field RollRatePID
typedef enum {
    STABILIZATIONSETTINGSBANK3_ROLLRATEPID_KP=0,
    STABILIZATIONSETTINGSBANK3_ROLLRATEPID_KI=1,
    STABILIZATIONSETTINGSBANK3_ROLLRATEPID_KD=2,
    STABILIZATIONSETTINGSBANK3_ROLLRATEPID_ILIMIT=3
} StabilizationSettingsBank3RollRatePIDElem;

// Number of elements for field RollRatePID
#define STABILIZATIONSETTINGSBANK3_ROLLRATEPID_NUMELEM 4

/* Field PitchRatePID information */

// Array element names for field PitchRatePID
typedef enum {
    STABILIZATIONSETTINGSBANK3_PITCHRATEPID_KP=0,
    STABILIZATIONSETTINGSBANK3_PITCHRATEPID_KI=1,
    STABILIZATIONSETTINGSBANK3_PITCHRATEPID_KD=2,
    STABILIZATIONSETTINGSBANK3_PITCHRATEPID_ILIMIT=3
} StabilizationSettingsBank3PitchRatePIDElem;

// Number of elements for field PitchRatePID
#define STABILIZATIONSETTINGSBANK3_PITCHRATEPID_NUMELEM 4

/* Field YawRatePID information */

// Array element names for field YawRatePID
typedef enum {
    STABILIZATIONSETTINGSBANK3_YAWRATEPID_KP=0,
    STABILIZATIONSETTINGSBANK3_YAWRATEPID_KI=1,
    STABILIZATIONSETTINGSBANK3_YAWRATEPID_KD=2,
    STABILIZATIONSETTINGSBANK3_YAWRATEPID_ILIMIT=3
} StabilizationSettingsBank3YawRatePIDElem;

// Number of elements for field YawRatePID
#define STABILIZATIONSETTINGSBANK3_YAWRATEPID_NUMELEM 4

/* Field RollPI information */

// Array element names for field RollPI
typedef enum {
    STABILIZATIONSETTINGSBANK3_ROLLPI_KP=0,
    STABILIZATIONSETTINGSBANK3_ROLLPI_KI=1,
    STABILIZATIONSETTINGSBANK3_ROLLPI_ILIMIT=2
} StabilizationSettingsBank3RollPIElem;

// Number of elements for field RollPI
#define STABILIZATIONSETTINGSBANK3_ROLLPI_NUMELEM 3

/* Field PitchPI information */

// Array element names for field PitchPI
typedef enum {
    STABILIZATIONSETTINGSBANK3_PITCHPI_KP=0,
    STABILIZATIONSETTINGSBANK3_PITCHPI_KI=1,
    STABILIZATIONSETTINGSBANK3_PITCHPI_ILIMIT=2
} StabilizationSettingsBank3PitchPIElem;

// Number of elements for field PitchPI
#define STABILIZATIONSETTINGSBANK3_PITCHPI_NUMELEM 3

/* Field YawPI information */

// Array element names for field YawPI
typedef enum {
    STABILIZATIONSETTINGSBANK3_YAWPI_KP=0,
    STABILIZATIONSETTINGSBANK3_YAWPI_KI=1,
    STABILIZATIONSETTINGSBANK3_YAWPI_ILIMIT=2
} StabilizationSettingsBank3YawPIElem;

// Number of elements for field YawPI
#define STABILIZATIONSETTINGSBANK3_YAWPI_NUMELEM 3

/* Field ManualRate information */

// Array element names for field ManualRate
typedef enum {
    STABILIZATIONSETTINGSBANK3_MANUALRATE_ROLL=0,
    STABILIZATIONSETTINGSBANK3_MANUALRATE_PITCH=1,
    STABILIZATIONSETTINGSBANK3_MANUALRATE_YAW=2
} StabilizationSettingsBank3ManualRateElem;

// Number of elements for field ManualRate
#define STABILIZATIONSETTINGSBANK3_MANUALRATE_NUMELEM 3

/* Field MaximumRate information */

// Array element names for field MaximumRate
typedef enum {
    STABILIZATIONSETTINGSBANK3_MAXIMUMRATE_ROLL=0,
    STABILIZATIONSETTINGSBANK3_MAXIMUMRATE_PITCH=1,
    STABILIZATIONSETTINGSBANK3_MAXIMUMRATE_YAW=2
} StabilizationSettingsBank3MaximumRateElem;

// Number of elements for field MaximumRate
#define STABILIZATIONSETTINGSBANK3_MAXIMUMRATE_NUMELEM 3

/* Field RollMax information */

/* Field PitchMax information */

/* Field YawMax information */

/* Field StickExpo information */

// Array element names for field StickExpo
typedef enum {
    STABILIZATIONSETTINGSBANK3_STICKEXPO_ROLL=0,
    STABILIZATIONSETTINGSBANK3_STICKEXPO_PITCH=1,
    STABILIZATIONSETTINGSBANK3_STICKEXPO_YAW=2
} StabilizationSettingsBank3StickExpoElem;

// Number of elements for field StickExpo
#define STABILIZATIONSETTINGSBANK3_STICKEXPO_NUMELEM 3

/* Field AcroInsanityFactor information */

// Array element names for field AcroInsanityFactor
typedef enum {
    STABILIZATIONSETTINGSBANK3_ACROINSANITYFACTOR_ROLL=0,
    STABILIZATIONSETTINGSBANK3_ACROINSANITYFACTOR_PITCH=1,
    STABILIZATIONSETTINGSBANK3_ACROINSANITYFACTOR_YAW=2
} StabilizationSettingsBank3AcroInsanityFactorElem;

// Number of elements for field AcroInsanityFactor
#define STABILIZATIONSETTINGSBANK3_ACROINSANITYFACTOR_NUMELEM 3

/* Field EnablePiroComp information */

// Enumeration options for field EnablePiroComp
typedef enum __attribute__ ((__packed__)) {
    STABILIZATIONSETTINGSBANK3_ENABLEPIROCOMP_FALSE=0,
    STABILIZATIONSETTINGSBANK3_ENABLEPIROCOMP_TRUE=1
} StabilizationSettingsBank3EnablePiroCompOptions;

/* Field FpvCamTiltCompensation information */

/* Field EnableThrustPIDScaling information */

// Enumeration options for field EnableThrustPIDScaling
typedef enum __attribute__ ((__packed__)) {
    STABILIZATIONSETTINGSBANK3_ENABLETHRUSTPIDSCALING_FALSE=0,
    STABILIZATIONSETTINGSBANK3_ENABLETHRUSTPIDSCALING_TRUE=1
} StabilizationSettingsBank3EnableThrustPIDScalingOptions;

/* Field ThrustPIDScaleCurve information */

// Array element names for field ThrustPIDScaleCurve
typedef enum {
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALECURVE_0=0,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALECURVE_25=1,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALECURVE_50=2,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALECURVE_75=3,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALECURVE_100=4
} StabilizationSettingsBank3ThrustPIDScaleCurveElem;

// Number of elements for field ThrustPIDScaleCurve
#define STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALECURVE_NUMELEM 5

/* Field ThrustPIDScaleSource information */

// Enumeration options for field ThrustPIDScaleSource
typedef enum __attribute__ ((__packed__)) {
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALESOURCE_MANUALCONTROLTHROTTLE=0,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALESOURCE_STABILIZATIONDESIREDTHRUST=1,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALESOURCE_ACTUATORDESIREDTHRUST=2
} StabilizationSettingsBank3ThrustPIDScaleSourceOptions;

/* Field ThrustPIDScaleTarget information */

// Enumeration options for field ThrustPIDScaleTarget
typedef enum __attribute__ ((__packed__)) {
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALETARGET_PID=0,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALETARGET_PI=1,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALETARGET_PD=2,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALETARGET_ID=3,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALETARGET_P=4,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALETARGET_I=5,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALETARGET_D=6
} StabilizationSettingsBank3ThrustPIDScaleTargetOptions;

/* Field ThrustPIDScaleAxes information */

// Enumeration options for field ThrustPIDScaleAxes
typedef enum __attribute__ ((__packed__)) {
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALEAXES_ROLLPITCHYAW=0,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALEAXES_ROLLPITCH=1,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALEAXES_ROLLYAW=2,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALEAXES_ROLL=3,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALEAXES_PITCHYAW=4,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALEAXES_PITCH=5,
    STABILIZATIONSETTINGSBANK3_THRUSTPIDSCALEAXES_YAW=6
} StabilizationSettingsBank3ThrustPIDScaleAxesOptions;



typedef struct __attribute__ ((__packed__)) {
    float Kp;
    float Ki;
    float Kd;
    float ILimit;
}  StabilizationSettingsBank3RollRatePIDData ;
typedef struct __attribute__ ((__packed__)) {
    float array[4];
}  StabilizationSettingsBank3RollRatePIDDataArray ;
#define StabilizationSettingsBank3RollRatePIDToArray( var ) UAVObjectFieldToArray( StabilizationSettingsBank3RollRatePIDData, var )

typedef struct __attribute__ ((__packed__)) {
    float Kp;
    float Ki;
    float Kd;
    float ILimit;
}  StabilizationSettingsBank3PitchRatePIDData ;
typedef struct __attribute__ ((__packed__)) {
    float array[4];
}  StabilizationSettingsBank3PitchRatePIDDataArray ;
#define StabilizationSettingsBank3PitchRatePIDToArray( var ) UAVObjectFieldToArray( StabilizationSettingsBank3PitchRatePIDData, var )

typedef struct __attribute__ ((__packed__)) {
    float Kp;
    float Ki;
    float Kd;
    float ILimit;
}  StabilizationSettingsBank3YawRatePIDData ;
typedef struct __attribute__ ((__packed__)) {
    float array[4];
}  StabilizationSettingsBank3YawRatePIDDataArray ;
#define StabilizationSettingsBank3YawRatePIDToArray( var ) UAVObjectFieldToArray( StabilizationSettingsBank3YawRatePIDData, var )

typedef struct __attribute__ ((__packed__)) {
    float Kp;
    float Ki;
    float ILimit;
}  StabilizationSettingsBank3RollPIData ;
typedef struct __attribute__ ((__packed__)) {
    float array[3];
}  StabilizationSettingsBank3RollPIDataArray ;
#define StabilizationSettingsBank3RollPIToArray( var ) UAVObjectFieldToArray( StabilizationSettingsBank3RollPIData, var )

typedef struct __attribute__ ((__packed__)) {
    float Kp;
    float Ki;
    float ILimit;
}  StabilizationSettingsBank3PitchPIData ;
typedef struct __attribute__ ((__packed__)) {
    float array[3];
}  StabilizationSettingsBank3PitchPIDataArray ;
#define StabilizationSettingsBank3PitchPIToArray( var ) UAVObjectFieldToArray( StabilizationSettingsBank3PitchPIData, var )

typedef struct __attribute__ ((__packed__)) {
    float Kp;
    float Ki;
    float ILimit;
}  StabilizationSettingsBank3YawPIData ;
typedef struct __attribute__ ((__packed__)) {
    float array[3];
}  StabilizationSettingsBank3YawPIDataArray ;
#define StabilizationSettingsBank3YawPIToArray( var ) UAVObjectFieldToArray( StabilizationSettingsBank3YawPIData, var )

typedef struct __attribute__ ((__packed__)) {
    uint16_t Roll;
    uint16_t Pitch;
    uint16_t Yaw;
}  StabilizationSettingsBank3ManualRateData ;
typedef struct __attribute__ ((__packed__)) {
    uint16_t array[3];
}  StabilizationSettingsBank3ManualRateDataArray ;
#define StabilizationSettingsBank3ManualRateToArray( var ) UAVObjectFieldToArray( StabilizationSettingsBank3ManualRateData, var )

typedef struct __attribute__ ((__packed__)) {
    uint16_t Roll;
    uint16_t Pitch;
    uint16_t Yaw;
}  StabilizationSettingsBank3MaximumRateData ;
typedef struct __attribute__ ((__packed__)) {
    uint16_t array[3];
}  StabilizationSettingsBank3MaximumRateDataArray ;
#define StabilizationSettingsBank3MaximumRateToArray( var ) UAVObjectFieldToArray( StabilizationSettingsBank3MaximumRateData, var )

typedef struct __attribute__ ((__packed__)) {
    int8_t Roll;
    int8_t Pitch;
    int8_t Yaw;
}  StabilizationSettingsBank3StickExpoData ;
typedef struct __attribute__ ((__packed__)) {
    int8_t array[3];
}  StabilizationSettingsBank3StickExpoDataArray ;
#define StabilizationSettingsBank3StickExpoToArray( var ) UAVObjectFieldToArray( StabilizationSettingsBank3StickExpoData, var )

typedef struct __attribute__ ((__packed__)) {
    uint8_t Roll;
    uint8_t Pitch;
    uint8_t Yaw;
}  StabilizationSettingsBank3AcroInsanityFactorData ;
typedef struct __attribute__ ((__packed__)) {
    uint8_t array[3];
}  StabilizationSettingsBank3AcroInsanityFactorDataArray ;
#define StabilizationSettingsBank3AcroInsanityFactorToArray( var ) UAVObjectFieldToArray( StabilizationSettingsBank3AcroInsanityFactorData, var )


/*
 * Packed Object data (unaligned).
 * Should only be used where 4 byte alignment can be guaranteed
 * (eg a single instance on the heap)
 */
typedef struct {
    StabilizationSettingsBank3RollRatePIDData RollRatePID;
    StabilizationSettingsBank3PitchRatePIDData PitchRatePID;
    StabilizationSettingsBank3YawRatePIDData YawRatePID;
    StabilizationSettingsBank3RollPIData RollPI;
    StabilizationSettingsBank3PitchPIData PitchPI;
    StabilizationSettingsBank3YawPIData YawPI;
    StabilizationSettingsBank3ManualRateData ManualRate;
    StabilizationSettingsBank3MaximumRateData MaximumRate;
    uint8_t RollMax;
    uint8_t PitchMax;
    uint8_t YawMax;
    StabilizationSettingsBank3StickExpoData StickExpo;
    StabilizationSettingsBank3AcroInsanityFactorData AcroInsanityFactor;
    StabilizationSettingsBank3EnablePiroCompOptions EnablePiroComp;
    uint8_t FpvCamTiltCompensation;
    StabilizationSettingsBank3EnableThrustPIDScalingOptions EnableThrustPIDScaling;
    int8_t ThrustPIDScaleCurve[5];
    StabilizationSettingsBank3ThrustPIDScaleSourceOptions ThrustPIDScaleSource;
    StabilizationSettingsBank3ThrustPIDScaleTargetOptions ThrustPIDScaleTarget;
    StabilizationSettingsBank3ThrustPIDScaleAxesOptions ThrustPIDScaleAxes;

} __attribute__((packed)) StabilizationSettingsBank3DataPacked;

/*
 * Packed Object data.
 * Alignment is forced to 4 bytes
 */
typedef StabilizationSettingsBank3DataPacked __attribute__((aligned(4))) StabilizationSettingsBank3Data;

/*
 * Union to apply the data array to and to use as structured object data
 */
union {
    StabilizationSettingsBank3DataPacked data;
    byte arr[STABILIZATIONSETTINGSBANK3_NUMBYTES];
 } StabilizationSettingsBank3DataUnion;

#endif // STABILIZATIONSETTINGSBANK3_H

/**
 * @}
 * @}
 */
