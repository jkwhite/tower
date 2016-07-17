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


public class Foodstuffs implements Mixin {
    private static int _chance = 20;


    public static void setChance(int chance) {
        _chance = chance;
    }

    public boolean match(Class c) {
        return NPC.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        NPC b = (NPC) o;
        int chance = 3;
        if(b.getForm() instanceof Humanoid) {
            chance = 30;
        }
        if(Rand.d100(chance)) {
            Item com = Universe.getUniverse().createItem(new ItemFilter() {
                public boolean accept(Item i, NHBot b) {
                    return i instanceof Comestible;
                }
            });
            while(Rand.d100(10)) {
                com.setCount(com.getCount()+1);
            }
            b.getInventory().add(com);
        }
    }
}
