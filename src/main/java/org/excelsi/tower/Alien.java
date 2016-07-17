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


public class Alien extends Mortal {
    public Alien(int power) {
        super(new AlienCorpse(),
            new Attach(power), new Skin(),
            new Slot(SlotType.useless, "body", 100));
    }

    public Alien() {
        this(1);
    }

    public void setPower(int power) {
        ((Attach)getNaturalWeapon()).setLeechPower(power);
    }

    public int getPower() {
        return ((Attach)getNaturalWeapon()).getLeechPower();
    }

    public Habitat getHabitat() {
        return Habitat.airborn;
    }

    private static class AlienCorpse extends Corpse {
        public int getFindRate() { return 10; }
        public float getWeight() { return 10f; }

        public void invoke(NHBot b) {
        }
    }
}
