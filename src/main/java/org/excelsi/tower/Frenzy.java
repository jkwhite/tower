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


public class Frenzy extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.cups, 3));
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(NHBot b) {
        float mult = 1.5f;
        boolean conf = false;
        switch(getStatus()) {
            case uncursed:
                N.narrative().print(b, Grammar.start(b, "go")+" into a rage!");
                //b.addAffliction(new Frenzied(new Modifier((int)(1.5*b.getStrength())), 1.5f));
                break;
            case blessed:
                mult = 2f;
                N.narrative().print(b, Grammar.start(b, "feel")+" incredibly powerful!");
                //b.addAffliction(new Frenzied(new Modifier(2*b.getStrength()), 2));
                break;
            case cursed:
                conf = true;
                N.narrative().print(b, Grammar.start(b, "go")+" into a blind rage!");
                //Frenzied f = new Frenzied(new Modifier((int)(1.5*b.getStrength())), 1.5f);
                //b.addAffliction(new Confused(f.getRemaining()));
                //b.addAffliction(f);
                break;
        }

        Frenzied f = new Frenzied(new Modifier(dose((int)(mult*b.getStrength())-b.getStrength())), mult);
        b.addAffliction(f);
        if(conf) {
            b.addAffliction(new Confused(f.getRemaining()));
        }
        b.addAffliction(new Overdose("frenzy", f.getRemaining()) {
            protected void finish() {
            }

            public String getStatus() {
                return null;
            }
        });
        return false;
    }
}
