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


public class TestWandOfWishing extends junit.framework.TestCase {
    public void testItems() {
        Item[] items = new Item[]{new Potion(new Strength()), new Pill(new Strength()), new ScrollOfMapping()};

        Universe u = new Universe();
        for(Item i:items) {
            u.add(i);
        }
        Universe.setUniverse(u);
        WandOfWishing w = new WandOfWishing();
        w.setCharges(Integer.MAX_VALUE);
        Patsy p = new Patsy();
        p.getInventory().add(w);
        for(Item i:items) {
            i.setClassIdentified(true);
            Request r = new Request(1, Status.uncursed, i.toString());
            assertTrue("couldn't wish for "+i, w.wish(p, r));
        }
        assertTrue("couldn't sloppy wish", w.wish(p, new Request(1, Status.uncursed, "ill of strength")));
        assertTrue("couldn't blessed wish", w.wish(p, new Request(1, Status.blessed, "pill of strength")));
        assertTrue("couldn't cursed wish", w.wish(p, new Request(1, Status.cursed, "pill of strength")));
        assertTrue("couldn't multi wish", w.wish(p, new Request(2, Status.uncursed, "scroll of mapping")));
        assertFalse("doesn't exist", w.wish(p, new Request(1, Status.uncursed, "happiness")));
    }
}
