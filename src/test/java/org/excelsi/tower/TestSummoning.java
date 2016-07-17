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
import org.excelsi.matrix.MatrixEnvironment;


public class TestSummoning extends junit.framework.TestCase {
    public void testSummon() {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        Patsy p = new Patsy();
        lev.getSpace(5,5).setOccupant(p);

        Universe u = new Universe();
        BasicBot sum = new BasicBot();
        sum.setMinLevel(3);
        sum.setMaxLevel(10);
        sum.setCommon("doorselfin");
        u.add(sum);
        Universe.setUniverse(u);

        ScrollOfSummoning s = new ScrollOfSummoning();
        s.invoke(p);
        MatrixEnvironment[] bots = lev.getBots();
        assertEquals("didn't add", 2, bots.length);
    }

    public void testNoRoomToSummon() {
        Level lev = new Level(20, 20);
        Patsy p = new Patsy();
        lev.setSpace(new Floor(), 5, 5);
        lev.getSpace(5,5).setOccupant(p);

        Universe u = new Universe();
        BasicBot sum = new BasicBot();
        sum.setMinLevel(3);
        sum.setMaxLevel(10);
        sum.setCommon("doorselfin");
        u.add(sum);
        Universe.setUniverse(u);

        ScrollOfSummoning s = new ScrollOfSummoning();
        s.invoke(p);
        MatrixEnvironment[] bots = lev.getBots();
        assertEquals("somehow added", 1, bots.length);
    }
}
