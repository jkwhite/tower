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


public class PitTrap extends Trap {
    public PitTrap() {
        super("pit", 50);
    }

    public Item[] getIngredients() {
        Item[] ing = new Item[]{new Board()};
        ing[0].setCount(4);
        return ing;
    }

    public boolean accept(NHSpace s) {
        if(s instanceof Pit) {
            return true;
        }
        else {
            N.narrative().print(s, "You need to dig a pit first!");
            return false;
        }
    }

    public Maneuver getDifficulty() {
        return Maneuver.medium;
    }

    public void trigger(NHBot b) {
        NHBot occ = getSpace().getOccupant();
        if(occ==null||occ.isLevitating()||occ.isAirborn()) {
            return;
        }
        NHSpace rep = (NHSpace) getSpace().replace(new Pit());
        N.narrative().print(b, Grammar.start(b, "fall")+" into a pit!");
        occ = rep.getOccupant();
        if(occ==null) {
            return;
        }
        int dmg = 1;
        while(dmg<100&&Rand.d100(101-occ.getModifiedAgility())) {
            dmg += Rand.om.nextInt(5)+1;
        }
        occ.setHp(Math.max(0, occ.getHp()-dmg));
        if(occ.getHp()==0) {
            N.narrative().more();
            occ.die("Fell into a pit");
        }
    }
}
