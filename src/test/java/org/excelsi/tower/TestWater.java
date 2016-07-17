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
import java.util.ArrayList;
import org.excelsi.matrix.*;


public class TestWater extends junit.framework.TestCase {
    public void testImmerse() {
        Water w = new Water(4, 0f);
        Patsy p = new Patsy();
        Potion potion = new Potion();
        w.immerse(p, potion);
        assertFalse("potion should not be empty", potion.isEmpty());
        assertEquals("should have water", new WaterInfliction(), potion.getInflictions().get(0));
    }

    public void testDissolveOccupant() {
        Rand.load();
        Water w = new Water(5, 0f);
        Item i = new ScrollOfEnchantWeapon();
        i.setCount(2);
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        p.getInventory().add(i);
        w.setOccupant(p);
        assertEquals("didn't dissolve", 1, i.getCount());
    }

    public void testDissolveDrop() {
        Rand.load();
        Water w = new Water(5, 0f);
        Item i = new ScrollOfEnchantWeapon();
        i.setCount(2);
        Patsy p = new Patsy();
        p.getInventory().add(i);
        w.add(i, p);

        assertEquals("water missing items", 1, w.numItems());
        assertEquals("wrong dissolved item", new BlankParchment(), w.getItem()[0]);
    }
}
