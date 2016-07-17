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


public final class Gold extends Item {
    //private int _amount;


    public Gold() {
        randomize();
    }

    public Gold(int amount) {
        setCount(amount);
    }

    public boolean equals(Object o) {
        return o instanceof Gold;
    }

    public int hashCode() {
        return 3214234;
    }

    public int score() {
        return 1;
    }

    public String getName() {
        return "gold";
    }

    public String getObscuredName() {
        if(getCount()==1) {
            return getName();
        }
        else if(getCount()<10) {
            return "few coin";
        }
        else if(getCount()<100) {
            return "pile of gold";
        }
        else {
            return "huge pile of gold";
        }
    }

    public Status getStatus() {
        return null; // gold has no status
    }

    public boolean isIdentified() {
        return true;
    }

    public boolean isClassIdentified() {
        return true;
    }

    public SlotType getSlotType() {
        return SlotType.none;
    }

    public Stat[] getStats() {
        return null;
    }

    public String getModel() {
        return "$";
    }

    public String getColor() {
        return "yellow";
    }

    public String getCategory() {
        return "-gold";
    }

    public DisplayType getDisplayType() {
        return DisplayType.status;
    }

    public StackType getStackType() {
        //return StackType.singular;
        return StackType.stackable;
    }

    public boolean isAlwaysSingular() {
        return true;
    }

    /*
    public void combine(Item i) {
        Gold g = (Gold) i;
        setAmount(getAmount()+g.getAmount());
    }
    */

    public String getKeyHint() {
        return "$";
    }

    public void invoke(NHBot invoker) {
    }

    /*
    public void setAmount(int amount) {
        //_amount = amount;
        setCount(amount);
    }
    */

    /*
    public int getAmount() {
        //return _amount;
        return getCount();
    }
    */

    public float getSize() {
        return .001f;
    }

    public float getWeight() {
        return .001f;
    }

    public void randomize() {
        setCount(Rand.om.nextInt(50)+3);
    }

    public String toString() {
        return Long.toString(getCount())+" gold";
    }

    public float getShininess() {
        return 5f;
    }
}
