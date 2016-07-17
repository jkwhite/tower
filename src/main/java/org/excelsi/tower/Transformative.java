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


public abstract class Transformative extends Variegated implements Fermionic {
    static {
        if(Universe.getUniverse()!=null&&Universe.getUniverse().getPublicColormap()!=null) {
            variegate("transformative", Universe.getUniverse().getPublicColormap().keySet().toArray(new String[0]));
        }
        else {
            // hack for unit test mode
            variegate("transformative", new String[]{"sleeping", "is", "giving", "in",
                "no", "matter", "what", "the", "time", "is", ",", "sleeping", "is",
                "giving", "in", "so", "lift", "those", "heavy", "eyelids"});
        }
    }

    public void setClassIdentified(boolean ident) {
        super.setClassIdentified(ident);
        if(ident) {
            //new Exception().printStackTrace();
        }
    }

    public String getName() {
        if(isClassIdentified()) {
            return super.getName();
        }
        else {
            return getColor();
        }
    }

    public String getText() {
        return getName();
    }

    public String getColor() {
        return getVariation("transformative");
    }

    public boolean apply(Item i, NHBot b) {
        if(super.apply(i, b)) {
            N.narrative().print(b, Grammar.first(Grammar.possessive(b, i))+" flashes "+getColor()+".");
            return true;
        }
        else {
            N.narrative().print(b, "Nothing happens.");
            return false;
        }
    }

}
