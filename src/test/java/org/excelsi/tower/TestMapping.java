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


public class TestMapping extends junit.framework.TestCase {
    public void testCursed() {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0,0,10,10,10,10), true);
        lev.addRoom(new Level.Room(14,14,4,4,20,20), true);
        Patsy p = new Patsy();
        lev.getSpace(5,5).setOccupant(p);
        assertEquals("wrong base knowledge", 11*11, p.getEnvironment().getKnown().size());
        p.getEnvironment().revealAll();
        assertEquals("wrong reveal knowledge", 11*11+5*5, p.getEnvironment().getKnown().size());
        ScrollOfMapping m = new ScrollOfMapping();
        m.setStatus(Status.cursed);
        m.invoke(p);
        assertEquals("wrong final knowledge", 11*11, p.getEnvironment().getKnown().size());
    }

    public void testUncursed() {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0,0,10,10,10,10), true);
        lev.addRoom(new Level.Room(14,14,4,4,20,20), true);
        Patsy p = new Patsy();
        lev.getSpace(5,5).setOccupant(p);
        assertEquals("wrong base knowledge", 11*11, p.getEnvironment().getKnown().size());
        ScrollOfMapping m = new ScrollOfMapping();
        m.invoke(p);
        assertEquals("wrong final knowledge", 11*11+5*5, p.getEnvironment().getKnown().size());
    }

    public void testBlessed() {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0,0,10,10,10,10), true);
        lev.addRoom(new Level.Room(14,14,4,4,20,20), true);
        Patsy p = new Patsy();
        lev.getSpace(5,5).setOccupant(p);
        assertEquals("wrong base knowledge", 11*11, p.getEnvironment().getKnown().size());
        ScrollOfMapping m = new ScrollOfMapping();
        m.setStatus(Status.blessed);
        m.invoke(p);
        assertEquals("wrong final knowledge", 11*11+5*5, p.getEnvironment().getKnown().size());
    }
}
