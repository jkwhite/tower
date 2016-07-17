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


public class TestTunneling extends junit.framework.TestCase {
    public void testTunnel() {
        Level lev = new Level(200, 200);
        lev.addRoom(new Level.Room(0, 0, 100, 100, 100, 100), true);
        Patsy p = new Patsy();
        Potion potion = new Potion(new Tunneling());
        lev.getSpace(50, 50).setOccupant(p);
        Quaff q = new Quaff(potion);
        q.setBot(p);
        q.perform();
        NHSpace s = p.getEnvironment().getMSpace();
        assertFalse("didn't tunnel: "+s, s.equals(lev.getSpace(50, 50)));
    }

    public void testCursedTunnel() {
        Level lev = new Level(200, 200);
        lev.addRoom(new Level.Room(0, 0, 100, 100, 100, 100), true);
        Patsy p = new Patsy();
        Potion potion = new Potion(new Tunneling());
        potion.setStatus(Status.cursed);
        lev.getSpace(50, 50).setOccupant(p);
        Quaff q = new Quaff(potion);
        q.setBot(p);
        q.perform();
        NHSpace s = p.getEnvironment().getMSpace();
        assertTrue("shouldn't have tunneled: "+s, s.equals(lev.getSpace(50, 50)));
        assertTrue("didn't afflict", p.isAfflictedBy("tunneling"));
        Rand.load();
        p.getEnvironment().face(Direction.east);
        DefaultNHBot.Forward f = new DefaultNHBot.Forward();
        f.setBot(p);
        f.perform();
        p.tick();
        assertFalse("should have tunneled on move: "+s, s.move(Direction.east).equals(p.getEnvironment().getMSpace()));
        int tries = 0;
        while(p.getAfflictions().size()>0) {
            if(++tries>1000) {
                fail("should have cured by now");
            }
            p.tick();
        }
    }
}
