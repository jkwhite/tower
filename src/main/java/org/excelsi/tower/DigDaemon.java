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
import org.excelsi.matrix.Direction;


public class DigDaemon extends WanderDaemon {
    private static Level.SpaceFilter FILTER = new Level.SpaceFilter() {
        public boolean accept(MSpace m) {
            return !(m instanceof ShopWall);
        }
    };

    public DigDaemon() {
        setFrequency(10);
        setTravel(75);
    }

    protected void approachDest() {
        if(_dest!=null) {
            Direction d = in.b.getEnvironment().getMSpace().directionTo(_dest);
            MSpace s = in.b.getEnvironment().getMSpace().move(d, true);
            //System.err.println("DEST: "+_dest);
            //System.err.println("FIRST: "+s);
            if(!s.isWalkable()&&FILTER.accept(s)) {
                Pick_Axe i = findTool();
                if(i!=null) {
                    i.dig(in.b, d);
                }
            }
            else {
                //System.err.println("CALLING SUPER");
                super.approachDest();
            }
        }
    }

    protected Pick_Axe findTool() {
        Item i = in.b.getWielded();
        if(i instanceof Pick_Axe) {
            return (Pick_Axe) i;
        }
        else {
            return null;
        }
        //for(Item i:in.b.getInventory().getItem()) {
        //}
    }
}
