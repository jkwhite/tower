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


public class TestVacuumMetastabilityDisaster extends junit.framework.TestCase {
    protected void setUp() {
        Rand.load();
    }

    public void testRestore() {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0,0,19,19,19,19), true);
        Patsy p = new Patsy();
        lev.getSpace(10,10).setOccupant(p);
        ScrollOfVacuumMetastabilityDisaster s = new ScrollOfVacuumMetastabilityDisaster();
        s.setCount(2);
        p.getInventory().add(s);
        s.invoke(p);
        s.invoke(p);
        assertEquals("didn't consume", 0, p.getInventory().numItems());
        int tries = 0;
        while(++tries<2000) {
            lev.tick();
        }
        if(p.isDead()) {
            fail("should have reversed");
        }
    }

    public void testInvoke() {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0,0,19,19,19,19), true);
        Patsy p = new Patsy();
        lev.getSpace(10,10).setOccupant(p);
        ScrollOfVacuumMetastabilityDisaster s = new ScrollOfVacuumMetastabilityDisaster();
        p.getInventory().add(s);
        s.invoke(p);
        assertEquals("didn't consume", 0, p.getInventory().numItems());
        int tries = 0;
        while(!p.isDead()) {
            lev.tick();
            if(++tries==2000) {
                fail("should have died by now");
            }
        }
    }
}
