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


public class Rock extends Gem implements Armament {
    private static final Stat[] STATS = new Stat[]{Stat.st, Stat.st, Stat.ag};


    public Rock() {
        setClassIdentified(true);
    }

    public boolean canCatalyze() {
        return false;
    }

    public float getShininess() {
        return 0f;
    }

    public void randomize() {
        super.randomize();
        setCount(Rand.om.nextInt(10)+1);
    }

    public String getName() {
        return "rock";
    }

    public String getColor() {
        return "gray";
    }

    public String getObscuredName() {
        return getName();
    }

    public float getWeight() {
        return 1f;
    }

    public float getSize() {
        return 1.5f;
    }

    public int getPower() {
        return 6;
    }

    public int score() {
        return 1;
    }

    public int getModifiedPower() {
        switch(getStatus()) {
            case blessed:
                return 8;
            case uncursed:
                return 6;
            case cursed:
                return 4;
        }
        return 4;
    }

    public int getRate() {
        return 30;
    }

    public Type getType() {
        return Type.missile;
    }

    public String getVerb() {
        return "hit";
    }

    public String getSkill() {
        return Weapon.THROWN;
    }

    public Stat[] getStats() {
        return STATS;
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        return null;
    }

    public void invoke(NHBot attacker, NHSpace s, Attack a) {
    }

    public Item toItem() {
        return this;
    }
}
