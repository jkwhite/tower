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


public class LaserRifle extends Gun implements Createable {
    public Item[] getIngredients() {
        Item[] ing = new Item[]{new Laser(), new Generator(), new Battery(), new Wire(), new ScrapMetal()};
        ing[1].setCount(2);
        ing[3].setCount(4);
        ing[4].setCount(2);
        return ing;
    }

    public String getCreationSkill() {
        return "gadgetry";
    }

    public boolean accept(NHSpace s) {
        return true;
    }

    public Maneuver getDifficulty() {
        return Maneuver.hard;
    }

    public String getColor() {
        return "silver";
    }

    public float getLevelWeight() { return 0.9f; }

    public float getSize() {
        return 2.5f;
    }

    public int getFindRate() {
        return 1;
    }

    public float getWeight() {
        return 3f;
    }

    public int getPower() {
        return 20;
    }

    public int getRate() {
        return 90;
    }

    public int getDistance() {
        return 15;
    }

    public String getRayAudio() {
        return "laser_med";
    }
}
