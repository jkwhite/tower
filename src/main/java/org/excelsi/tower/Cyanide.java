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


public class Cyanide extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.cups, 5));
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(NHBot b) {
        if(b.getForm() instanceof Mech) {
            N.narrative().print(b, Grammar.first(Grammar.toBe(b))+" unaffected.");
        }
        else {
            switch(getStatus()) {
                case uncursed:
                    N.narrative().print(b, "That was cyanide.");
                    kill(b);
                    break;
                case blessed:
                    N.narrative().print(b, "That was cyanide.");
                    if(Rand.d100(90)) {
                        kill(b);
                    }
                    else {
                        N.narrative().print(b, "Miraculously, "+Grammar.noun(b)+" "+Grammar.conjugate(b, "seem")+" unaffected.");
                    }
                    break;
                case cursed:
                    N.narrative().print(b, "That was cyanide.");
                    kill(b);
                    break;
            }
        }
        return false;
    }

    public int getOccurrence() {
        return 0;
    }

    private void kill(NHBot b) {
        if(getOwner() instanceof Pill || getOwner() instanceof Potion || getOwner() instanceof Explosion) {
            getOwner().setClassIdentified(true);
            b.die("Killed by "+Grammar.nonspecific(getOwner()));
        }
        else {
            b.die("Killed by a cyanide-covered "+getOwnerName());
        }
    }
}
