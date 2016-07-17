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


public class Poison extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.cups, 4));
    }

    public static final String NAME = "poison";

    private Poisons _p;


    public Poison() {
        _p = Poisons.random();
    }

    public Poison(Poisons p) {
        _p = p;
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(NHBot b) {
        if(!b.isAfflictedBy(Poisoned.NAME)) {
            b.addAffliction(new Poisoned(_p, Rand.om.nextInt(10)+10, getOwner()!=null?getOwner().toString():null));
            N.narrative().print(b, "Poison courses through "+Grammar.possessive(b)+" body.");
            if(_p==Poisons.luck&&b.isPlayer()) {
                N.narrative().print(b, "You feel a deep sense of dread.");
            }
        }
        return false;
    }

    public int getOccurrence() {
        return 10;
    }
}
