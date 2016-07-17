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


public class Flashlight extends Light implements Createable {
    public Flashlight() {
        super(12000);
    }

    public Item[] getIngredients() {
        Item[] ing = new Item[]{new Battery(), new LightBulb(), new Wire()};
        ing[2].setCount(2);
        return ing;
    }

    public boolean accept(NHSpace s) {
        return true;
    }

    public String getCreationSkill() {
        return "gadgetry";
    }

    public Maneuver getDifficulty() {
        return Maneuver.light;
    }

    public float getSize() {
        return 0.3f;
    }

    public float getWeight() {
        return 0.2f;
    }

    public String getColor() {
        return "gray";
    }

    public float getCandela() {
        switch(getStatus()) {
            case blessed:
                return 6f;
            default:
            case uncursed:
                return 5f;
            case cursed:
                return 2f;
        }
    }

    public int getFindRate() {
        return 8;
    }
}
