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


public class TestBattery extends junit.framework.TestCase {
    public void testBlessed() {
        WandOfLightning w = new WandOfLightning();
        w.setCharges(0);
        Patsy p = new Patsy();
        p.getInventory().add(w);
        Battery b = new Battery();
        b.setStatus(Status.blessed);
        p.getInventory().add(b);
        b.invoke(p);
        assertTrue("didn't charge", w.getCharges()>3);
        assertFalse("didn't consume", p.getInventory().contains(b));
    }

    public void testWishing() {
        WandOfWishing w = new WandOfWishing();
        w.setCharges(0);
        Patsy p = new Patsy();
        p.getInventory().add(w);
        Battery b = new Battery();
        b.setUses(10);
        p.getInventory().add(b);
        Rand.load();
        b.invoke(p);
        assertEquals("didn't charge to 1", 1, w.getCharges());
        assertFalse("didn't consume", p.getInventory().contains(b));
    }

    public void testUncursed() {
        WandOfLightning w = new WandOfLightning();
        w.setCharges(0);
        Patsy p = new Patsy();
        p.getInventory().add(w);
        Battery b = new Battery();
        p.getInventory().add(b);
        b.invoke(p);
        assertTrue("didn't charge", w.getCharges()>0);
        assertFalse("didn't consume", p.getInventory().contains(b));
    }

    public void testCursed() {
        WandOfLightning w = new WandOfLightning();
        w.setCharges(0);
        Patsy p = new Patsy();
        p.getInventory().add(w);
        Battery b = new Battery();
        b.setStatus(Status.cursed);
        p.getInventory().add(b);
        Rand.load();
        b.invoke(p);
        assertFalse("didn't corrode", p.getInventory().contains(w));
        assertFalse("didn't consume", p.getInventory().contains(b));
    }
}
