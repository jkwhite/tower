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
import java.util.Arrays;
import java.util.ArrayList;
import org.excelsi.matrix.Direction;


public class TestShrines extends junit.framework.TestCase {
    public void testMatch() {
        assertTrue("doesn't match level", new Basins().match(Level.class));
    }

    public void testPlace() {
        Level lev = new Level(10, 10);
        lev.setSpace(new Floor(), 5, 5);
        Shrines s = new Shrines();
        Rand.load();
        s.mix(lev);
        assertTrue("didn't replace", lev.getSpace(5,5) instanceof Shrine);
    }
}
