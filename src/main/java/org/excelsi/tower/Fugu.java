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


public class Fugu extends Mortal {
    public Fugu() {
        super(new FuguCorpse(),
              new Bite(10),
              new Skin(),
              new Slot(SlotType.useless, "body", 100),
              new Slot(SlotType.eyes, "eyes", 0)
        );
    }

    public Habitat getHabitat() {
        return Habitat.aquatic;
    }

    private static class FuguCorpse extends Corpse {
        public Status getStatus() { return Status.cursed; }
        public int getFindRate() { return 80; }
        public float getSize() { return 2; }

        public void invoke(NHBot b) {
            b.addAffliction(new Poisoned(Poisons.nervous, 500, "tetrodotoxin"));
            if(b.isPlayer()) {
                N.narrative().print(b, Grammar.first(Grammar.possessive(b))+" lips tingle.");
            }
        }
    }
}
