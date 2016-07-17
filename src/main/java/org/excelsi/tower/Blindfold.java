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


public class Blindfold extends Tool implements Affector {
    public String getColor() {
        return "black";
    }

    public float getSize() {
        return 0;
    }

    public float getWeight() {
        return 0.01f;
    }

    public int getRate() {
        return 0;
    }

    public int getPower() {
        return 0;
    }

    public int getModifiedPower() {
        return 0;
    }

    public int getFindRate() {
        return 10;
    }

    public SlotType getSlotType() {
        return SlotType.eyes;
    }

    public void use(NHBot b) {
        if(!b.isEquipped(this)) {
            Wear w = new Wear();
            w.setItem(this);
            w.setBot(b);
            w.perform();
        }
        else {
            Takeoff t = new Takeoff();
            t.setItem(this);
            t.setBot(b);
            t.perform();
        }
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        return null;
    }

    public void invoke(NHBot attacker, NHSpace defender, Attack a) {
    }

    public Item toItem() {
        return this;
    }

    public String getSkill() {
        return null;
    }

    public String getVerb() {
        return null;
    }

    public Type getType() {
        return Type.melee;
    }

    public void attach(NHBot b) {
        b.setBlind(b.getBlind()+1);
    }

    public void remove(NHBot b) {
        b.setBlind(b.getBlind()-1);
    }
}
