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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public class Whirlpool extends Hole {
    private static final long serialVersionUID = 1L;


    public Whirlpool() {
        super("blue");
    }

    public String getModel() {
        return "~";
    }

    public void fall() {
        NHBot b = getOccupant();
        Level lev = b.getEnvironment().getFloor(b.getEnvironment().getLevel()-1);
        int i = getI(), j = getJ();
        MSpace to = lev.getSpace(i, j);
        if(to==null) {
            to = new Water();
            try {
                lev.setSpace((MatrixMSpace)to, i, j);
            }
            catch(IndexOutOfBoundsException e) {
            }
        }
        else if(to.isReplaceable() && !(to instanceof Water)) {
            to = to.replace(new Water());
        }
        if(Rand.d100(101-b.getModifiedConstitution())) {
            if(b.isPlayer()) {
                N.narrative().print(b, "Urk...");
            }
            ((Container)to).add(new Consume.Vomit());
        }
        super.fall();
    }

    protected void printMessage(NHBot b) {
        N.narrative().print(b, Grammar.start(b, "get")+" sucked down the whirlpool!");
    }
}
