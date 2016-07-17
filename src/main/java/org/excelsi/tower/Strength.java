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


public class Strength extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.swords, 0));
    }

    public Strength() {
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(NHBot b) {
        switch(getStatus()) {
            case uncursed:
                b.setStrength(b.getStrength()+dose(1));
                N.narrative().print(b, Grammar.start(b, "feel")+" stronger.");
                break;
            case blessed:
                b.setStrength(b.getStrength()+dose(Rand.om.nextInt(2)+1));
                N.narrative().print(b, Grammar.start(b, "feel")+" stronger.");
                break;
            case cursed:
                b.setStrength(b.getStrength()+dose(10));
                b.addAffliction(new Overdose("strength") {
                    protected void finish() {
                        getBot().setStrength(Math.max(0, getBot().getStrength()-dose(15)));
                        N.narrative().print(getBot(), Grammar.start(getBot())+" don't feel so good.");
                        getBot().addAffliction(new Confused());
                    }

                    public String getStatus() {
                        return null;
                    }
                });
                N.narrative().print(b, Grammar.start(b, "feel")+" an unnatural power coursing through "+Grammar.possessive(b)+" body.");
                break;
        }
        return true;
    }
}
