/*
    Tower
    Copyright (C) 2007, John K White, All Rights Reserved
*/
/*
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
package org.excelsi.tower;


import org.excelsi.aether.*;
import org.excelsi.matrix.Direction;


/**
 * Admittedly this must be some kind of futuristic
 * jackhammer that weighs less than half of a current
 * one and operates without pneumatic hose.
 */
public class Jackhammer extends Pick_Axe {
    public float getSize() {
        return 3;
    }

    public float getWeight() {
        return 10;
    }

    public int getPower() {
        return 5;
    }

    public int getRate() {
        return 10;
    }

    public String getSkill() {
        return "blunt instruments";
    }

    public int getDiggingRate() {
        return 3;
    }

    public int getFindRate() {
        return 5;
    }
}
