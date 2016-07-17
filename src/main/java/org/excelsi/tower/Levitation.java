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
import org.excelsi.matrix.MSpace;


public class Levitation extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.swords, 3));
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(NHBot b) {
        if(!b.isLevitating()) {
            N.narrative().print(b, Grammar.start(b)+" "+Grammar.conjugate(b, "begin")+" to float in the air.");
            int time = Rand.om.nextInt(40)+40;
            switch(getStatus()) {
                case cursed:
                    time /= 2;
                    break;
                case blessed:
                    time *= 2;
            }
            b.addAffliction(new Levitating(time));
        }
        else {
            Affliction a = b.getAffliction(Levitating.NAME);
            if(!a.isStuck()) {
                b.removeAffliction(Levitating.NAME);
                endEffect(b);
            }
            else {
                N.narrative().print(b, "Nothing happens.");
            }
        }
        return true;
    }

    static void endEffect(NHBot b) {
        b.setLevitating(false);
        N.narrative().print(b, Grammar.start(b)+" "+Grammar.conjugate(b, "float")+" gently to the ground.");
    }

    public int getOccurrence() {
        return 35;
    }
}
