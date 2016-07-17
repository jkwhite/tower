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


public class Morl extends Mortal {
    public Morl() {
        super(new MorlCorpse(),
            new Fist(4), new Flameskin(),
            new Slot(SlotType.useless, "body", 100));
    }

    private static class MorlCorpse extends Corpse {
        public int getFindRate() { return 10; }
        public float getWeight() { return 0.2f; }
        public float getSize() { return 0.2f; }

        public void invoke(NHBot b) {
            N.narrative().print(b, Grammar.first(Grammar.possessive(b))+" stomach is on fire!");
            for(int i=0;i<4;i++) {
                new Fire().inflict(b, new Source("eating a morl corpse"));
            }
        }
    }

    private static class Flameskin extends Skin {
        public Flameskin() {
        }

        public int getRate() {
            return 100;
        }

        public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
            N.narrative().printf(defender, "Flames erupt from %p skin!", defender);
            return new BoltAttack(new Fire(800), attacker, new Source(Grammar.possessive(defender)+" flameskin"));
        }
    }
}
