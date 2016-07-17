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


public class Blackberry extends Fruit {
    public float getSize() {
        return 0.02f;
    }

    public float getWeight() {
        return 0.01f;
    }

    public int getNutrition() {
        return Hunger.RATE/10;
    }

    public String getColor() {
        return "black";
    }

    public int getFindRate() {
        return 10;
    }

    public Seed toSeed() {
        return new BlackberrySeed(getFragments());
    }
}
