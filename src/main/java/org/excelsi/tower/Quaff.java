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


public class Quaff extends ItemAction implements InstantaneousAction, SpaceAction {
    public Quaff() {
        super("drink", new ItemFilter() {
            public boolean accept(Item i, NHBot b) {
                return i instanceof Potion && ! ((Potion)i).isEmpty();
            }
        });
    }

    public Quaff(Potion i) {
        super("drink", i);
    }

    public String getDescription() {
        return "Drink from a potion or water source.";
    }

    public boolean isPerformable(NHBot b) {
        NHSpace s = b.getEnvironment().getMSpace();
        if(s instanceof Drinkable) {
            return true;
        }
        for(Parasite p:s.getParasites()) {
            if(p instanceof Drinkable) {
                return true;
            }
        }
        return false;
    }

    // NEXT: add context
    protected void act() {
        N.narrative().print(getBot(), Grammar.start(getBot())+" "+Grammar.conjugate(getBot(), getVerb())+" "+
            Grammar.singular(getItem())+".");
        if(getBot().isPlayer()) {
            getItem().setClassIdentified(true);
        }
        getItem().invoke(getBot());
    }

    @Override protected boolean useSpace(final Context c) {
        if(c.actor().getEnvironment()!=null) {
            NHSpace s = c.actor().getEnvironment().getMSpace();
            for(Parasite p:s.getParasites()) {
                if(p instanceof Drinkable) {
                    Drinkable d = (Drinkable) p;
                    if(c.n().confirm(c.actor(), "Drink from the "+d.getName()+"?")) {
                        d.drink(c.actor());
                        return true;
                    }
                }
            }
            if(s instanceof Drinkable) {
                Drinkable d = (Drinkable) s;
                if(c.n().confirm(c.actor(), "Drink from the "+d.getName()+"?")) {
                    d.drink(c.actor());
                    return true;
                }
            }
        }
        return false;
    }
}
