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


public class Piscean extends Mortal {
    public Piscean() {
        super(new PisceanCorpse(),
              new Bite(10),
              new Skin(),
              new Slot(SlotType.useless, "body", 100),
              new Slot(SlotType.eyes, "eyes", 0)
        );
    }

    public Habitat getHabitat() {
        return Habitat.aquatic;
    }

    public void invoke(NHBot b) {
        // unlike most corpses raw fish can usually be consumed by humanoids
        // without issue.
        if(b.getForm() instanceof Humanoid && Rand.d100((Math.max(1,75-b.getModifiedConstitution())))) {
            b.addAffliction(new Delay(new Nauseous(Rand.om.nextInt(25)+10), 5+Rand.om.nextInt(15)));
        }
    }

    private static class PisceanCorpse extends Corpse {
        public int getFindRate() { return 20; }
        public float getSize() { return 2; }
        public float getDecayRate() { return 0.5f; }
    }
}
