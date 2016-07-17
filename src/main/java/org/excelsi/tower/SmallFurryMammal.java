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


public class SmallFurryMammal extends Mortal {
    public SmallFurryMammal() {
        super(new SmallFurryMammalCorpse(),
              new Bite(4),
              new Skin(),
              new Slot(SlotType.useless, "body", 100),
              new Slot(SlotType.eyes, "eyes", 0)
        );
    }

    private static class SmallFurryMammalCorpse extends Corpse {
        public int getFindRate() { return 5; }
        public float getWeight() { return 0.2f; }
    }

    public Habitat getHabitat() {
        return Habitat.hydrophobic;
    }
}
