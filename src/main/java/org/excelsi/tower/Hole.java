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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public class Hole extends Floor /*DefaultNHSpace*/ {
    private static final long serialVersionUID = 1L;
    private GearChange _g = new GearChange();


    public Hole() {
        this("black");
    }

    public Hole(String color) {
        super(color);
        addMSpaceListener(new MSpaceAdapter() {
            public void occupied(MSpace source, Bot b) {
                b.addListener(_g);
                fall();
            }

            public void unoccupied(MSpace source, Bot b) {
                b.removeListener(_g);
            }

            public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
                if(to==Hole.this) {
                    b.addListener(_g);
                    fall();
                }
                else {
                    b.removeListener(_g);
                }
            }
        });
    }

    public int getDepth() {
        return 6;
    }

    public int getHeight() {
        return -getDepth();
    }

    public NHSpace getTo() {
        NHBot b = getOccupant();
        if(b==null) {
            b = (NHBot) Actor.current();
        }
        Level lev = b.getEnvironment().getFloor(b.getEnvironment().getLevel()-1);
        int i = getI(), j = getJ();
        MSpace to = null;
        try {
            to = lev.getSpace(i, j);
        }
        catch(Exception e) {
        }
        if(to==null) {
            to = new Ground();
            try {
                lev.setSpace((MatrixMSpace)to, i, j);
            }
            catch(IndexOutOfBoundsException e) {
                i = Rand.om.nextInt(lev.width()-2)+1;
                j = Rand.om.nextInt(lev.height()-2)+1;
                to = lev.getSpace(i, j);
                if(to==null) {
                    to = new Ground();
                    lev.setSpace((MatrixMSpace)to, i, j);
                }
            }
        }
        else {
            if(!to.isWalkable()||to.isOccupied()) {
                //to = to.replace(new Ground());
                to = lev.findNearestEmpty(new Level.SpaceFilter() {
                    public boolean accept(MSpace m) {
                        return m instanceof Floor || m instanceof Ground;
                    }
                }, (MatrixMSpace) to);
            }
        }
        return (NHSpace) to;
    }

    public void fall() {
        NHBot b = getOccupant();
        if(b.isLevitating()||b.isAirborn()||b.getModifiedWeight()<1f) {
            N.narrative().printf(b, "%V over the hole.", b, "float");
            return;
        }
        Level lev = b.getEnvironment().getFloor(b.getEnvironment().getLevel()-1);
        MSpace to = getTo();
        printMessage(b);
        if(b.isPlayer()) {
            b.getEnvironment().setLevel(lev.getFloor(), to);
        }
        else {
            to.setOccupant(b);
        }
    }

    public boolean look(NHBot b) {
        N.narrative().print(b, "There is a hole here.");
        return true;
    }

    public String getModel() {
        return " ";
    }

    public boolean isTransparent() {
        return true;
    }

    public boolean isWalkable() {
        return true;
    }

    public int add(Item i) {
        //int ret = super.add(i);
        //destroy(i);
        int ret = getTo().add(i);
        return ret;
    }

    public int add(Item i, NHBot adder) {
        //int ret = super.add(i, adder);
        //destroy(i);
        int ret = getTo().add(i, adder);
        return ret;
    }

    public void addLoot(Container loot) {
        if(loot!=null) {
            for(Item i:loot.getItem()) {
                add(i);
            }
        }
    }

    protected void printMessage(NHBot b) {
        N.narrative().print(b, Grammar.start(b, "fall")+" through the hole!");
    }

    private class GearChange extends NHEnvironmentAdapter {
        public void equipped(NHBot b, Item i) {
            fall();
        }

        public void unequipped(NHBot b, Item i) {
            fall();
        }
    }
}
