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
package org.excelsi.matrix;


public class TestDirection extends junit.framework.TestCase {
    public void testTwirl() {
        int dirs = 8;
        Direction d = Direction.north;
        for(int i=0;i<dirs;i++) {
            d = d.right();
        }
        for(int i=0;i<dirs;i++) {
            d = d.left();
        }
        assertEquals("didn't twirl correctly", Direction.north, d);
        d.distance(); // TODO: remove?
    }

    public void testUpDown() {
        assertEquals("turn", Direction.up, Direction.down.right());
        assertEquals("turn", Direction.up, Direction.down.left());
        assertEquals("turn", Direction.down, Direction.up.right());
        assertEquals("turn", Direction.down, Direction.up.left());
    }

    public void testOpposing() {
        assertEquals("opposing", Direction.north, Direction.south.opposing());
        assertEquals("opposing", Direction.northwest, Direction.southeast.opposing());
        assertEquals("opposing", Direction.northeast, Direction.southwest.opposing());
        assertEquals("opposing", Direction.west, Direction.east.opposing());
        assertEquals("opposing", Direction.east, Direction.west.opposing());
        assertEquals("opposing", Direction.south, Direction.north.opposing());
        assertEquals("opposing", Direction.southwest, Direction.northeast.opposing());
        assertEquals("opposing", Direction.southeast, Direction.northwest.opposing());
        assertEquals("opposing", Direction.up, Direction.down.opposing());
        assertEquals("opposing", Direction.down, Direction.up.opposing());
    }
}
