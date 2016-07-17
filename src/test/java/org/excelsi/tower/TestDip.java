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


public class TestDip extends junit.framework.TestCase {
    public void testCleanse() {
        Cake c = new Cake();
        c.setStatus(Status.cursed);
        Potion p = new Potion(new WaterInfliction());
        p.setStatus(Status.blessed);
        Patsy b = new Patsy();
        Dip d = new Dip(c, p);
        d.setBot(b);
        assertEquals("cake got messed up", Status.cursed, c.getStatus());
        d.perform();
        assertEquals("did not cleanse", Status.uncursed, c.getStatus());
    }

    public void testAutoBasin() {
        Rand.load();
        Basin b = new Basin(true);
        Patsy p = new Patsy();
        p.setForm(new Canid());
        Cake c = new Cake();
        c.setStatus(Status.cursed);
        p.getInventory().add(c);
        Level lev = new Level(10, 10);
        lev.setSpace(b, 5, 5);
        b.setOccupant(p);

        Dip d = new Dip();
        d.setBot(p);
        d.perform();
        assertEquals("didn't cleanse", Status.uncursed, c.getStatus());
    }
}
