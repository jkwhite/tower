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


public class Autocrossbow extends Crossbow implements Createable {
    public Modifier getModifier() {
        Modifier m = new Modifier();
        m.setQuickness(15);
        m.setAgility(10);
        return m;
    }

    public Maneuver getDifficulty() {
        return Maneuver.veryhard;
    }

    public String getCreationSkill() {
        return "gadgetry";
    }

    public Item[] getIngredients() {
        Item[] ing = new Item[]{new Crossbow(), new ScrapMetal(), new Board(), new Nail(), new Spring()};
        ing[0].setCount(2);
        ing[1].setCount(2);
        ing[3].setCount(6);
        return ing;
    }

    public boolean accept(NHSpace s) {
        return true;
    }

    public int getPower() {
        return 32;
    }

    public int getRate() {
        return 90;
    }

    public float getWeight() {
        return 6f;
    }

    public float getSize() {
        return 3f;
    }

    public String getColor() {
        return "gray";
    }

    public int getFindRate() {
        return 1;
    }
}
