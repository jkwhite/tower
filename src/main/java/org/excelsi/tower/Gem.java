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
import org.excelsi.matrix.Actor;
import java.util.Map;
import java.util.HashMap;


public abstract class Gem extends Item {
    private static final String[] DAMAGES = {"fractured ", "cracked ", "chipped ",
        "flawed ", "tarnished "};


    public String[] getDamageNames() {
        return DAMAGES;
    }

    public String getName() {
        return isClassIdentified()?super.getName():getColor()+" gem";
    }

    public String getObscuredName() {
        return getCategory();
    }

    public boolean isNatural() {
        return true;
    }

    public boolean isUnstable() {
        return false;
    }

    public Item catalyze(Item i, Container container) {
        Item c = internalCatalyze();
        c.setStatus(getStatus());
        int cost = 10;
        switch(getStatus()) {
            case blessed:
                cost /=2 ;
                break;
            case cursed:
                cost += cost/2;
                break;
        }
        setHp(Math.max(0, getHp()-cost));
        if(getHp()==0) {
            if(Actor.current()!=null) {
                N.narrative().printf((NHBot)Actor.current(), "The gem shatters!");
            }
            container.consume(this);
        }
        return c;
    }

    public final boolean supports(Item i) {
        return canCatalyze() && (i instanceof BlankParchment);
    }

    protected Item internalCatalyze() {
        return null;
    }

    protected boolean canCatalyze() {
        return true;
    }

    public final StackType getStackType() {
        return StackType.stackable;
    }

    public final SlotType getSlotType() {
        return SlotType.none;
    }

    public final String getCategory() {
        return "gem";
    }

    public float getSize() {
        return 0.05f;
    }

    public float getWeight() {
        return 0.1f;
    }

    public final String getModel() {
        return "*";
    }

    public Stat[] getStats() {
        return null;
    }

    public void invoke(NHBot b) {
    }

    public float getShininess() {
        return 5f;
    }
}
