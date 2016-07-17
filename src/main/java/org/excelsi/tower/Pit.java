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


public class Pit extends Ground implements Flooring {
    public Pit() {
        //setDepth(6);
    }

    public int getDepth() {
        return 6;
    }

    public int getHeight() {
        return -getDepth();
    }

    public boolean isTransparent() {
        return true;
    }

    protected boolean canLeave(MSpace to) {
        if(to instanceof Pit||getOccupant().isAirborn()||getOccupant().isLevitating()||numItems()>=4||!isAdjacentTo(to)) {
            return true;
        }
        if(Rand.d100(1+getOccupant().getModifiedAgility()/5)) {
            if(getOccupant().isPlayer()) {
                N.narrative().print(getOccupant(), Grammar.start(getOccupant(), "crawl")+" to the edge of the "+getNoun()+".");
            }
            return true;
        }
        else {
            if(getOccupant().isPlayer()) {
                N.narrative().print(getOccupant(), Grammar.startToBe(getOccupant())+" stuck in a "+getNoun()+".");
            }
            return false;
        }
    }

    private String getNoun() {
        for(MSpace m:cardinal()) {
            if(m instanceof Pit) {
                return "trench";
            }
        }
        return "pit";
    }
}
