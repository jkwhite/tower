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


public class Dip extends ItemAction {
    private Immersion _into;


    public Dip() {
        super("dip", new ItemFilter() {
            public boolean accept(Item i, NHBot bot) {
                return true;
            }
        }, true);
    }

    public Dip(Item i, Immersion into) {
        super("dip", i);
        _into = into;
    }

    public String getDescription() {
        return "Dip an item in a potion or water source.";
    }

    protected void act() {
        Item chosen = getItem();
        //System.err.println("COUNT: "+chosen.getCount());
        String utensil = chosen!=null?Grammar.noun(chosen):"your hand";

        Immersion i = _into;
        if(chosen==null) {
            chosen = (Item) getBot().getForm().getNaturalWeapon();
        }
        if(i==null) {
            NHSpace s = getBot().getEnvironment().getMSpace();
            if(s instanceof Immersion && !((Immersion)s).isEmpty()) {
                String n = ((Immersion)s).getName();
                if(N.narrative().confirm(getBot(), "There is "+n+" here. Dip "+utensil+" in the "+n+"?")) {
                    i = (Immersion) s;
                }
            }
            if(i==null) {
                i = (Immersion) N.narrative().choose(getBot(), new ItemConstraints(getBot().getInventory(),
                    "What do you want to dip the "+chosen.getName()+" into?",
                    "That is not a liquid.",
                    new InstanceofFilter(Immersion.class)), false);
            }
        }
        boolean split = false;
        // why armaments? because you should be able to dip a bunch of darts
        // or arrows in a basin and coat them all with the infliction. it's
        // kind of a hack.
        if(!(chosen instanceof Armament)&&getBot().getInventory().contains(chosen)) {
            chosen = getBot().getInventory().split(chosen);
            split = true;
        }
        i.immerse(getBot(), chosen);
        if(split) {
            if(chosen.getCount()>0) {
                getBot().getInventory().add(chosen);
            }
        }
        _into = null;
    }
}
