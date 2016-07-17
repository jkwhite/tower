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
package org.excelsi.aether;


import org.excelsi.matrix.Direction;


public class TestLevel extends junit.framework.TestCase {
    public void testFlooring() { // TODO when MSpace.push is implemented
        Level lev = new Level(20, 20);
        lev.setSpace(new Floor(), 2, 2);
        lev.setSpace(new Floor(), 3, 2);
        lev.setSpace(new Floor(), 4, 2);
        lev.getSpace(3,2).push(null, Direction.north);

        //assertTrue("didn't stretch", lev.getSpace(2,1) instanceof Floor);
        //assertTrue("didn't stretch", lev.getSpace(3,1) instanceof Floor);

        //lev.getSpace(3,1).push(new Floor(), Direction.north);
    }

    public void testWalls() {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        assertEquals("nw", "+", lev.getSpace(0,0).getModel());
        assertEquals("sw", "+", lev.getSpace(0,10).getModel());
        assertEquals("ne", "+", lev.getSpace(10,0).getModel());
        assertEquals("se", "+", lev.getSpace(10,10).getModel());
        assertEquals("n", "-", lev.getSpace(5,0).getModel());
        assertEquals("s", "-", lev.getSpace(5,10).getModel());
        assertEquals("w", "|", lev.getSpace(0,5).getModel());
        assertEquals("e", "|", lev.getSpace(10,5).getModel());
    }
}
