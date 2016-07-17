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
package org.excelsi.aether;


import java.util.ArrayList;
import java.util.Arrays;


public class TestInventory extends junit.framework.TestCase {
    public void testSplit() {
        Inventory i = new Inventory();
        i.setKeyed(true);
        Item it = TestItem.createItem("19th of july", "21st of may");
        it.setCount(3);
        i.add(it);
        String k = i.keyFor(it);
        Item it2 = i.split(it);
        assertEquals("wrong count for orig", 2, it.getCount());
        assertEquals("wrong count for split", 1, it2.getCount());
        it2.setName("when you stop being missed");
        i.add(it2);
        assertEquals("orig key changed", k, i.keyFor(it));
        assertFalse("dupe keys", k.equals(i.keyFor(it2)));
        assertEquals("wrong keyfilter", i.keyFor(it)+i.keyFor(it2), i.validKeys(new CategoryFilter("21st of may"), null));
    }

    public void testCategorize() {
        Inventory i = new Inventory();
        i.add(TestItem.createItem("tundra", "desert"));
        i.add(TestItem.createItem("novocaine", "stain"));
        i.add(TestItem.createItem("cowboy", "dan"));
        i.add(TestItem.createItem("black", "cadillacs"));
        i.add(TestItem.createItem("white", "cadillacs"));

        Inventory.Category[] cats = i.categorize();
        assertEquals("wrong cat count", 4, cats.length);
        Inventory.Category c = null;
        for(Inventory.Category cat:cats) {
            if(cat.getName().equals("desert")) {
                c = cat;
                break;
            }
        }
        assertEquals("wrong num in 0", 1, c.getItems().length);
        assertEquals("wrong num in 0", 1, c.size());
        assertEquals("wrong item", "tundra", c.getItem(0).getName());
    }
}
