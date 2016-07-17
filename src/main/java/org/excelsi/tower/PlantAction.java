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
import org.excelsi.matrix.*;


public class PlantAction extends DefaultNHBotAction implements SpaceAction {
    private Object _s;


    public PlantAction(Object s) {
        _s = s;
    }

    public PlantAction() {
    }

    public String getDescription() {
        return "Plant a seed.";
    }

    public boolean isPerformable(NHBot b) {
        return true;
    }

    public void perform() {
        NHSpace ms = (NHSpace) getBot().getEnvironment().getMSpace();
        if(!(ms instanceof Fertile)) {
            N.narrative().print(getBot(), "Seeds need fertile ground to grow!");
            return;
        }
        for(Parasite p:ms.getParasites()) {
            if(p instanceof Plant || p instanceof Seed) {
                N.narrative().print(getBot(), "Something is already growing here!");
                return;
            }
        }
        try {
            String noun = null;
            if(_s==null) {
                _s = N.narrative().choose(getBot(), new ItemConstraints(getBot().getInventory(), "plant", new Multifilter(new InstanceofFilter(Seed.class), new InstanceofFilter(Plantable.class))), false);
            }
            Parasite seed = null;
            if(_s instanceof Seed) {
                seed = (Seed) _s;
            }
            else {
                seed = ((Plantable)_s).toSeed();
                getBot().getInventory().consume((Item)_s);
            }
            getBot().getEnvironment().getMSpace().addParasite(seed);
            N.narrative().print(getBot(), Grammar.start(getBot(), "plant")+" "+Grammar.noun((Item)_s)+".");
        }
        finally {
            _s = null;
        }
    }

    public String toString() {
        return "Plant";
    }
}
