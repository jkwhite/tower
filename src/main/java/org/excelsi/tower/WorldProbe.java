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
import org.excelsi.matrix.MSpace;


public class WorldProbe extends Tool implements Armament {
    private static final Stat[] STATS = new Stat[]{Stat.st, Stat.st, Stat.ag};


    public WorldProbe() {
    }

    public float getSize() {
        return 0.1f;
    }

    public float getWeight() {
        return 0.1f;
    }

    public int getPower() {
        return 0;
    }

    public int getRate() {
        return 0;
    }

    public String getVerb() {
        return "modulate";
    }

    public String getColor() {
        return "white";
    }

    public Stat[] getStats() {
        return STATS;
    }

    public int getModifiedPower() {
        return getPower();
    }

    public String getSkill() {
        return null;
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        return null;
    }

    public void invoke(NHBot attacker, NHSpace s, Attack a) {
    }

    public Type getType() {
        return Type.melee;
    }

    public Item toItem() {
        return this;
    }

    public int getFindRate() {
        return 0;
    }

    public boolean isObtainable() {
        return false;
    }

    public boolean isUnique() {
        return true;
    }

    public void use(NHBot b) {
        Direction d = N.narrative().direct(b, "Which direction?");
        MSpace s = b.getEnvironment().getMSpace().creator();
        s = s.move(d);
        if(s.isOccupied()) {
        }
    }
}
