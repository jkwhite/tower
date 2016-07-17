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


public class Skin extends Inflicter implements Armament {
    public Skin() {
        super();
    }

    public Skin(Infliction i) {
        super(i);
    }

    public int getPower() {
        return 0;
    }

    public int getModifiedPower() {
        return 0;
    }

    public int getRate() {
        return 0;
    }

    public float getWeight() {
        return 0f;
    }

    public float getSize() {
        return 0f;
    }

    public void invoke(NHBot b) {
    }

    public SlotType getSlotType() {
        return SlotType.none;
    }

    public String getCategory() {
        return null;
    }

    public Type getType() {
        return Type.melee;
    }

    /**
     * Overrides to no-op, because damage taken
     * here is accounted for in the bot's hp.
     */
    public void setHp(int hp) {
    }

    public String getVerb() {
        return "block";
    }

    public Stat[] getStats() {
        return null;
    }

    public String getSkill() {
        return "unarmored";
    }

    public String getColor() {
        return null;
    }

    public String getModel() {
        return null;
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        inflict(attacker);
        return null;
    }

    public void invoke(NHBot attacker, NHSpace s, Attack a) {
    }

    public Item toItem() {
        return null;
    }
}
