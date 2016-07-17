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
import org.excelsi.matrix.Direction;


public class Untrap extends DefaultNHBotAction {
    public String getDescription() {
        return "Try to disarm a trap.";
    }

    public void perform() {
        Direction d = N.narrative().direct(getBot(), "Which direction?");
        NHSpace sp = (NHSpace) getBot().getEnvironment().getMSpace().move(d, true);

        for(Parasite p:sp.getParasites()) {
            if(p instanceof Trap && !p.isHidden()) {
                int skill = getBot().getSkill("traps");
                if(Rand.d100(skill)) {
                    sp.removeParasite(p);
                    N.narrative().print(getBot(), Grammar.start(getBot(), "disarm")+" the "+((Trap)p).getName()+".");
                    if(p instanceof Createable) {
                        Item[] ings = ((Createable)p).getIngredients();
                        if(ings!=null) {
                            for(Item ing:ings) {
                                if(ing.getCount()>1) {
                                    ing.setCount(1+Rand.om.nextInt(ing.getCount()));
                                }
                                sp.add(ing);
                            }
                        }
                    }
                    if(getBot() instanceof Patsy) {
                        Patsy patsy = (Patsy) getBot();
                        if(patsy.getSkill("traps")>0) {
                            if(!patsy.getCatalogue("traps").contains(p.getClass().getName())) {
                                patsy.getCatalogue("traps").add(p.getClass().getName());
                                N.narrative().print(getBot(), "The "+p.toString()+" gives you an idea...");
                            }
                        }
                    }
                }
                else {
                    N.narrative().print(getBot(), "Oops!");
                    p.trigger(getBot());
                }
                getBot().skillUp("traps");
                return;
            }
        }
        N.narrative().print(getBot(), "There is no trap there.");
    }
}
