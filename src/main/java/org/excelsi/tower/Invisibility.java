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


public class Invisibility extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.swords, 7));
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(NHBot b) {
        int time = Rand.om.nextInt(120)+54;
        switch(getStatus()) {
            case cursed:
                time *= 2;
                break;
            case blessed:
                time /= 2;
                break;
        }
        b.addAffliction(new Invisible(time));
        //if(!b.isAfflictedBy(Invisible.NAME)) {
            //N.narrative().print(b, Grammar.start(b, "fade")+" from view.");
        //}
        return false;
    }
}
