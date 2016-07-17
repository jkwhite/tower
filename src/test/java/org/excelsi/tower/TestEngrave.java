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


public class TestEngrave extends junit.framework.TestCase {
    public void testWand() {
        Patsy p = new Patsy();
        Floor f = new Floor();
        WandOfFire w = new WandOfFire();
        int ch = w.getCharges();
        p.getInventory().add(w);
        f.setOccupant(p);
        Engrave e = new Engrave(w, "heat of the land");
        e.setBot(p);
        e.perform();
        assertEquals("should have been able to write", 1, f.getParasites().size());
        assertTrue("didn't use any wand charge", w.getCharges()<ch);
    }

    public void testFingers() {
        Patsy p = new Patsy();
        Floor f = new Floor();
        f.setOccupant(p);
        Engrave e = new Engrave(null, "swallowing sweat");
        e.setBot(p);
        e.perform();
        assertEquals("didn't get parasite", 1, f.getParasites().size());
    }

    public void testChalk() {
        Patsy p = new Patsy();
        Floor f = new Floor();
        PieceOfChalk c = new PieceOfChalk();
        int charges = c.getCharges();
        p.getInventory().add(c);
        f.setOccupant(p);
        Engrave e = new Engrave(c, "say those dreams can shake me");
        e.setBot(p);
        e.perform();
        assertEquals("didn't get parasite", 1, f.getParasites().size());
        assertTrue("didn't use any chalk", c.getCharges()<charges);
    }

    public void testEmptyWand() {
        Patsy p = new Patsy();
        Floor f = new Floor();
        WandOfFire w = new WandOfFire();
        p.getInventory().add(w);
        w.setCharges(0);
        f.setOccupant(p);
        Engrave e = new Engrave(w, "dawn come down and save me");
        e.setBot(p);
        e.perform();
        assertEquals("shouldn't have been able to write", 0, f.getParasites().size());
    }
}
