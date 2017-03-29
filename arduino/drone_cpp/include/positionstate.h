/**
 ******************************************************************************
 * @addtogroup UAVObjects LibrePilot UAVObjects
 * @{
 * @addtogroup PositionState PositionState
 * @brief Contains the estimate of the current position relative to @ref HomeLocation, in NED coordinates
 *
 * Autogenerated files and functions for PositionState Object
 *
 * @{
 *
 * @file       positionstate.h
 *
 * @author     The LibrePilot Project, https://www.librepilot.org, (C) 2017.
 *             The OpenPilot Team, http://www.openpilot.org Copyright (C) 2010-2013.
 *
 * @brief      Arduino Header of the PositionState object. This file has been
 *             automatically generated by the UAVObjectGenerator.
 *
 * @note       Object definition file: positionstate.xml.
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

#ifndef POSITIONSTATE_H
#define POSITIONSTATE_H
#include <stdbool.h>
/* Object constants */
#define POSITIONSTATE_OBJID 0x4AFDB658
#define POSITIONSTATE_ISSINGLEINST 1
#define POSITIONSTATE_ISSETTINGS 0
#define POSITIONSTATE_ISPRIORITY 0
#define POSITIONSTATE_NUMBYTES sizeof(PositionStateData)

/* Field North information */

/* Field East information */

/* Field Down information */




/*
 * Packed Object data (unaligned).
 * Should only be used where 4 byte alignment can be guaranteed
 * (eg a single instance on the heap)
 */
typedef struct {
    float North;
    float East;
    float Down;

} __attribute__((packed)) PositionStateDataPacked;

/*
 * Packed Object data.
 * Alignment is forced to 4 bytes
 */
typedef PositionStateDataPacked __attribute__((aligned(4))) PositionStateData;

/*
 * Union to apply the data array to and to use as structured object data
 */
union {
    PositionStateDataPacked data;
    byte arr[POSITIONSTATE_NUMBYTES];
 } PositionStateDataUnion;

#endif // POSITIONSTATE_H

/**
 * @}
 * @}
 */
