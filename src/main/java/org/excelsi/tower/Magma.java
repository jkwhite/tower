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


public class Magma extends Liquid {
    private static final long serialVersionUID = 1L;


    public Magma() {
        this(8, 0f);
    }

    public Magma(int depth, float cycle) {
        super("orange", depth, cycle);
    }

    public String getColor() {
        return getDepth()<=4?"yellow":"orange";
    }

    public int add(Item i) {
        int ret = super.add(i);
        destroy(i);
        return ret;
    }

    public int add(Item i, NHBot adder) {
        int ret = super.add(i, adder);
        destroy(i);
        return ret;
    }

    public void addLoot(Container loot) {
        if(loot!=null) {
            for(Item i:loot.getItem()) {
                add(i);
            }
        }
    }

    public void immerse(NHBot b, Item i) {
        new Fire(9999).inflict(b);
    }

    public String getName() {
        return "magma";
    }

    protected void soak(boolean say) {
        if(getOccupant().getModifiedWeight()<1) {
            if(say) {
                N.narrative().print(getOccupant(), Grammar.start(getOccupant(), "float")+" on the magma's surface!");
                boolean fr = false;
                if(getOccupant() instanceof Elemental) {
                    fr = ((Elemental)getOccupant()).getElements().contains(Element.fire);
                }
                if(fr) {
                    if(getOccupant().isPlayer()) {
                        N.narrative().print(getOccupant(), "The flames flicker around you harmlessly.");
                    }
                }
                else {
                    N.narrative().print(getOccupant(), "Not that it helps much.");
                }
            }
        }
        if(!getOccupant().isLevitating()&&!getOccupant().isAirborn()) {
            new Fire(9999).inflict(getOccupant());
        }
    }

    protected Magma spread() {
        return new Magma(getDepth(), getCycle());
    }
}
