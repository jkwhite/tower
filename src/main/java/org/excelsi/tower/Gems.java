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


public class Gems implements Mixin {
    private static int _chance = 92;


    public static void setChance(int chance) {
        _chance = chance;
    }

    public boolean match(Class c) {
        return Level.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        Level lev = (Level) o;
        while(Rand.d100(_chance)) {
            MSpace m = null;
            int x = 0, y = 0;
            int tries = 0;
            do {
                x = Rand.om.nextInt(lev.width());
                y = Rand.om.nextInt(lev.height());
                m = lev.getSpace(x, y);
            } while(m instanceof Wall || m instanceof Doorway || (m instanceof Floor && Rand.om.nextBoolean()));
            if(m==null) {
                Blank b = new Blank();
                lev.setSpace(b, x, y);
                m = b;
            }
            ((NHSpace)m).add(Universe.getUniverse().createItem(new ItemFilter() {
                public boolean accept(Item i, NHBot b) {
                    return i instanceof Gem;
                }
            }));
        }
    }
}
