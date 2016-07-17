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


public class Eye extends Mortal {
    public Eye(Armament naturalWeapon) {
        super(new EyeCorpse(),
            naturalWeapon, null,
            new Slot(SlotType.useless, "body", 100),
            new Slot(SlotType.eyes, "eye", 0));
        setNaturalArmor(new Skin(new Gaze(true)));
    }

    public Eye() {
        this(new Fist(4));
    }

    private static class EyeCorpse extends Corpse {
        public int getFindRate() { return 70; }
        public float getWeight() { return 10f; }
        public float getSize() { return 8f; }
        public void invoke(NHBot b) {
            if(b.getConnected()==Connected.none) {
                b.setConnected(Connected.weak);
                HelmOfConnection.connect(b);
            }
        }
    }

    class Gaze extends Infliction {
        public Gaze() {
        }

        public Gaze(boolean identified) {
            setIdentified(true);
        }

        public boolean inflict(NHSpace s) {
            return false;
        }

        public boolean inflict(NHBot b) {
            if(Rand.d100()>b.getModifiedSelfDiscipline()&&b.getBlind()==0) {
                N.narrative().print(b, Grammar.startToBe(b)+" frozen by the "+getName()+"'s gaze!");
                b.start(new Frozen(b));
            }
            return true;
        }

        public String getName() {
            return "frozen";
        }

        public String getText() {
            return "freezing";
        }

        public GrammarType getPartOfSpeech() {
            return GrammarType.adjective;
        }
    }
}
