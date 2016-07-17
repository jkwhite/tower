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
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Filter;


public class BoardTrap extends Trap {
    public BoardTrap() {
        super("board", 30);
    }

    public Item[] getIngredients() {
        Item[] ings = new Item[] {new Board(), new Nail()};
        ings[1].setCount(2);
        return ings;
    }

    public boolean accept(NHSpace s) {
        return true;
    }

    public Maneuver getDifficulty() {
        return Maneuver.light;
    }

    public void trigger(NHBot b) {
        boolean reveal = false;
        //NHBot occ = getSpace().getOccupant();
        NHBot occ = b;
        if(occ!=null) {
            if(!occ.isAirborn()&&!occ.isLevitating()&&occ.getModifiedWeight()>0) {
                reveal = true;
                occ.setAudible(occ.getAudible()+7);
                if(b.getEnvironment().getPlayer().getEnvironment().getVisible().contains(getSpace())) {
                    N.narrative().print(b, "A board beneath "+Grammar.noun(b)+" squeaks loudly!");
                }
                else {
                    N.narrative().print(b.getEnvironment().getPlayer(), "You hear a distant squeak.");
                }
            }
        }
        else {
            if(b.getEnvironment().getPlayer().getEnvironment().getVisible().contains(getSpace())) {
                N.narrative().print(b, "A board squeaks loudly!");
                reveal = true;
            }
            else {
                N.narrative().print(b.getEnvironment().getPlayer(), "You hear a distant squeak.");
            }
        }
        if(reveal&&(b.isPlayer()||b.getEnvironment().getPlayer().getEnvironment().getVisible().contains(getSpace()))) {
            setHidden(false);
        }
    }
}
