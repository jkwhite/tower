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
import java.util.Map;
import java.util.HashMap;


/**
 * Pills are capsules possibly carrying inflictions in a solid form.
 */
public class Pill extends Inflicter implements Soluble {
    public Pill() {
    }

    public Pill(Infliction i) {
        super(i);
    }

    public String getObscuredName() {
        return getCategory();
    }

    public String getName() {
        if(getInflictions().size()>0) {
            return super.getName();
        }
        else {
            return "empty capsule";
        }
    }

    public Fragment.GrammarType partOfSpeech(Fragment f) {
        return Fragment.GrammarType.adjective;
    }

    public final StackType getStackType() {
        return StackType.stackable;
    }

    public final SlotType getSlotType() {
        return SlotType.useless;
    }

    public String getColor() {
        if(getInflictions().size()>0) {
            if(getInflictions().get(0) instanceof Transformative) {
                return ((Transformative)getInflictions().get(0)).getColor();
            }
            else {
                return "gray";
            }
        }
        return "light-blue";
    }

    public final String getCategory() {
        return "pill";
    }

    public Stat[] getStats() {
        return null;
    }

    public float getSize() {
        //return 0.05f;
        return 0.4f;
    }

    public boolean isEmpty() {
        return getInflictions().size()==0;
    }

    public float getWeight() {
        return isEmpty()?0.01f:0.05f;
    }

    public final String getModel() {
        return ",";
    }

    public int getFindRate() {
        return getFragments().size()>0?100:50;
    }

    public void invoke(NHBot b) {
        inflict(b, false);
    }

    public boolean dissolve(NHBot b, Container c, Item solution, boolean deliberate, boolean all) {
        int count = getCount();
        if(!all) {
            count = 1;
        }
        if(deliberate||b==null||b.getInventory()==null||!b.getInventory().contains(this)) {
            if(count==1) {
                N.narrative().print(b, "The "+getName()+" dissolves.");
            }
            else {
                N.narrative().print(b, "The "+Grammar.pluralize(getName())+" dissolve.");
            }
        }
        else {
            if(b.getInventory().contains(this)) {
                if(count>1) {
                    N.narrative().print(b, Grammar.first(Grammar.possessive(b))+" "+Grammar.pluralize(getName())+" dissolve!");
                }
                else {
                    N.narrative().print(b, Grammar.first(Grammar.possessive(b, this))+" dissolves!");
                }
            }
        }
        if(solution!=null) {
            for(Fragment f:getFragments()) {
                solution.addFragment(f);
            }
            if(c.contains(solution)) {
                c.remove(solution);
                c.add(solution);
            }
        }
        if(all) {
            //c.remove(this);
            setCount(0);
        }
        else {
            //c.consume(this);
            setCount(0);
        }
        if(solution==null) {
            c.consume(this);
        }
        return false;
    }
}
