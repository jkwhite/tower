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


public class Dragonkin extends Mortal {
    public Dragonkin(Armament naturalWeapon) {
        super(new DragonCorpse(naturalWeapon),
            naturalWeapon,
            new Skin(),
            new Slot(SlotType.useless, "body", 100),
            new Slot(SlotType.useless, "eyes", 0));
    }

    public Dragonkin() {
        this(new Bite(4));
    }

    public static class DragonCorpse extends Corpse {
        private Armament _w;

        public DragonCorpse(Armament naturalWeapon) {
            _w = naturalWeapon;
        }

        public int getFindRate() { return 80; }
        public float getWeight() { return 20f; }

        public void invoke(NHBot b) {
            _w.invoke(null, b, null);
        }
    }
}
