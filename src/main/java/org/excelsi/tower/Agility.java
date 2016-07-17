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


public class Agility extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.swords, 2));
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(NHBot b) {
        switch(getStatus()) {
            case uncursed:
                b.setAgility(b.getAgility()+dose(1));
                N.narrative().print(b, Grammar.start(b, "feel")+" more agile.");
                break;
            case blessed:
                b.setAgility(b.getAgility()+dose(Rand.om.nextInt(2)+1));
                N.narrative().print(b, Grammar.start(b, "feel")+" more agile.");
                break;
            case cursed:
                b.setAgility(b.getAgility()-dose(Rand.om.nextInt(2)+1));
                N.narrative().print(b, Grammar.start(b, "feel")+" less agile.");
                break;
        }
        return true;
    }
}
