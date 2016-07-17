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
import org.excelsi.matrix.MSpace;


public class TestFabricator extends junit.framework.TestCase {
    public void testFabricator() {
        Fabricator fab = new Fabricator();
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        lev.setSpace(fab, 5, 5);
        Patsy p = new Patsy();
        fab.setOccupant(p);
        BasicBot b = new BasicBot();
        Corpse c = new Slime().toCorpse();
        fab.add(c, p);
        assertEquals("shouldn't record", 0, fab.getKnowns().size());

        fab.use(p);

        c = new Slime().toCorpse();
        b = new BasicBot();
        b.setCommon("");
        b.setModel("you sit outside and you wonder if");
        b.setColor("you can make it on your own");
        c.setSpirit(b);
        Universe.setUniverse(new Universe());
        Universe.getUniverse().add(DefaultNHBot.copy(b));
        fab.add(c, p);
        assertEquals("should record", 1, fab.getKnowns().size());

        fab.use(p);
        assertEquals("should have fabricated", 2, lev.getBots().length);
    }
}
