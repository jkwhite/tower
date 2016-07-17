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
import java.util.List;


public abstract class Book extends VariegatedItem implements Combustible, Laminatable {
    static {
        if(Universe.getUniverse()!=null&&Universe.getUniverse().getPublicColormap()!=null) {
            variegate("book", Universe.getUniverse().getPublicColormap().keySet().toArray(new String[0]));
        }
        else {
            variegate("book", new String[]{"yellow", "magenta", "cyan", "black"});
        }
    }

    public String getColor() {
        return getVariation();
    }

    public boolean isCombustible() {
        return true;
    }

    public int getCombustionTemperature() {
        return isLaminated()?800:451;
    }

    public final String getCombustionPhrase() {
        return "catches fire and burns";
    }

    public void combust(Container c) {
        c.consume(this);
    }

    public String getName() {
        if(isIdentified()||isClassIdentified()) {
            return super.getName();
        }
        else {
            return getColor()+" "+getCategory();
        }
    }

    public String getObscuredName() {
        return "book";
    }

    public final void setLaminated(boolean laminated) {
        if(laminated) {
            addFragment(new Laminated());
        }
        else {
            removeFragment(Laminated.NAME);
        }
    }

    public final boolean isLaminated() {
        return hasFragment(Laminated.class);
    }

    public final SlotType getSlotType() {
        return SlotType.useless;
    }

    public Stat[] getStats() {
        return new Stat[]{Stat.re};
    }

    public final String getModel() {
        return "i+";
    }

    public final String getCategory() {
        return "book";
    }

    public float getSize() {
        return 2f;
    }

    public float getWeight() {
        return 3f;
    }

    public void invoke(NHBot b) {
    }
}
