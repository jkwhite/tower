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


public class Slime extends Mortal {
    public Slime(Armament naturalWeapon) {
        super(new SlimeCorpse(),
            naturalWeapon, new Goo(),
            new Slot(SlotType.useless, "body", 100));
    }

    public Slime() {
        this(new Touch(4));
    }

    private static class SlimeCorpse extends Corpse {
        public int getFindRate() { return 10; }
        public float getWeight() { return 0.2f; }
        public float getSize() { return 0.2f; }

        public void invoke(NHBot b) {
            b.setStrength(b.getStrength()/(Rand.om.nextInt(2)+2));
            if(b.isPlayer()) {
                N.narrative().print(b, "Your limbs suddenly seem much heavier.");
            }
            else {
                N.narrative().print(b, Grammar.start(b)+" looks frail.");
            }
        }
    }

    private static class Goo extends Skin {
        public Goo() {
            addFragment(new Sap(true));
        }
    }

    static class Sap extends Infliction {
        public Sap() {
        }

        public Sap(boolean identified) {
            setIdentified(identified);
        }

        public boolean inflict(NHSpace s) {
            return false;
        }

        public boolean inflict(NHBot b) {
            if(Rand.d100(10)) {
                b.setStrength(Math.max(b.getStrength()-1, 0));
                N.narrative().print(b, Grammar.start(b, "feel")+" weaker.");
            }
            return true;
        }

        public String getName() {
            return "sap";
        }

        public String getText() {
            return "sapping";
        }

        public GrammarType getPartOfSpeech() {
            return GrammarType.nounPhrase;
        }
    }
}
