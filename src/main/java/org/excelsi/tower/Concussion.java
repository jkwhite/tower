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


public class Concussion extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.cups, 0));
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(NHBot b) {
        switch(getStatus()) {
            case uncursed:
                b.setMaxHp(b.getMaxHp()+Rand.om.nextInt(5)+5);
                N.narrative().print(b, Grammar.start(b, "feel")+" healthier.");
                break;
            case blessed:
                b.setMaxHp(b.getMaxHp()+Rand.om.nextInt(10)+5);
                N.narrative().print(b, Grammar.start(b, "feel")+" healthier.");
                break;
            case cursed:
                b.setMaxHp(2*b.getMaxHp());
                b.setHp(2*b.getHp());
                b.addAffliction(new Overdose("concussion", Rand.om.nextInt(20)+10) {
                    protected void finish() {
                        getBot().setMaxHp(getBot().getMaxHp()/3);
                        getBot().setHp(getBot().getHp()/3);
                        N.narrative().print(getBot(), Grammar.first(Grammar.possessive(getBot())+" power fades."));
                    }

                    public String getStatus() {
                        return null;
                    }
                });
                N.narrative().print(b, Grammar.start(b, "feel")+" great! Really, really great!");
                break;
        }
        return false;
    }
}
