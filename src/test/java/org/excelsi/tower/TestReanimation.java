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


public class TestReanimation extends junit.framework.TestCase {
    protected void setUp() {
        Rand.load();
    }

    public void testAutoReanimate() {
        Level lev = new Level(20, 20);
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        ScrollOfReanimation re = new ScrollOfReanimation();
        p.getInventory().add(re);
        BasicBot b = new BasicBot();
        create(lev, p, b, re);
        Read r = new Read();
        r.setBot(p);
        r.perform();

        b.die("divine whimsy");
        assertTrue("didn't die", b.isDead());
        assertFalse("didn't unoccupy", lev.getSpace(1,2).isOccupied());
        assertEquals("didn't leave corpse", 1, lev.getSpace(1,2).numItems());
        r.setItem(re);
        r.setBot(p);
        r.perform();
        assertFalse("didn't resurrect", b.isDead());
    }

    public void testReanimate() {
        Level lev = new Level(20, 20);
        Patsy p = new Patsy();
        BasicBot b = new BasicBot();
        ScrollOfReanimation re = new ScrollOfReanimation();
        create(lev, p, b, re);
        Read r = new Read(re);
        r.setBot(p);
        r.perform();

        b.die("divine whimsy");
        assertTrue("didn't die", b.isDead());
        assertFalse("didn't unoccupy", lev.getSpace(1,2).isOccupied());
        assertEquals("didn't leave corpse", 1, lev.getSpace(1,2).numItems());
        r.setItem(re);
        r.setBot(p);
        r.perform();
        assertFalse("didn't resurrect", b.isDead());
    }

    public void testCursedReanimate() {
        Level lev = new Level(20, 20);
        Patsy p = new Patsy();
        BasicBot b = new BasicBot();
        b.setThreat(p, Threat.friendly);
        ScrollOfReanimation re = new ScrollOfReanimation();
        re.setStatus(Status.cursed);
        create(lev, p, b, re);

        b.die("divine whimsy");
        Read r = new Read(re);
        r.setBot(p);
        r.perform();
        assertFalse("didn't resurrect", b.isDead());
        assertEquals("didn't go horribly awry", Threat.kos, b.threat(p));
    }

    public void testBlessedReanimate() {
        Level lev = new Level(20, 20);
        Patsy p = new Patsy();
        BasicBot b = new BasicBot();
        b.setThreat(p, Threat.kos);
        ScrollOfReanimation re = new ScrollOfReanimation();
        re.setStatus(Status.blessed);
        create(lev, p, b, re);

        b.die("divine whimsy");
        Read r = new Read(re);
        r.setBot(p);
        r.perform();
        assertFalse("didn't resurrect", b.isDead());
        assertEquals("didn't turn all happy sunshines", Threat.friendly, b.threat(p));
    }

    private void create(Level lev, Patsy p, BasicBot b, ScrollOfReanimation re) {
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        lev.getSpace(1,1).setOccupant(p);
        new Corpses().mix(b);
        b.setForm(new Canid());
        lev.getSpace(1,2).setOccupant(b);
        re.setCount(2);
        p.getInventory().add(re);
    }
}
