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


public class Crossbow extends Weapon2H implements Createable {
    private static final Stat[] STATS = new Stat[]{Stat.st, Stat.ag, Stat.ag};

    public Item[] getIngredients() {
        Item[] ing = new Item[]{new ScrapMetal(), new Board(), new BallOfYarn(), new Spring()};
        ing[1].setCount(2);
        ing[2].setCount(2);
        return ing;
    }

    public float getLevelWeight() { return 0.2f; }

    public boolean accept(NHSpace s) {
        return true;
    }

    public String getCreationSkill() {
        return "gadgetry";
    }

    public Maneuver getDifficulty() {
        return Maneuver.medium;
    }

    public final String getSkill() {
        return BOWS;
    }

    public Type getType() {
        return Type.missile;
    }

    public Stat[] getStats() {
        return STATS;
    }

    public int getPower() {
        return 6;
    }

    public int getRate() {
        return 70;
    }

    public float getWeight() {
        return 6f;
    }

    public float getSize() {
        return 3f;
    }

    public String getColor() {
        return "dark-brown";
    }
}
