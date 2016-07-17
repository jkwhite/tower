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


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Direction;
import org.excelsi.matrix.Matrix;
import org.excelsi.matrix.MatrixMSpace;


public class TestSpaces extends junit.framework.TestCase {
    public void testStairs() {
        Stairs s = new Stairs(true);
        assertEquals("wrong model", "<", s.getModel());
        s = new Stairs(false);
        assertEquals("wrong model", ">", s.getModel());
        assertEquals("color", "gray", s.getColor());
    }

    public void testDoorway() {
        Doorway d = new Doorway(true);
        final StringBuilder ev = new StringBuilder();
        d.addMSpaceListener(new NHSpaceAdapter() {
            public void attributeChanged(NHSpace s, String attr, Object oldValue, Object newValue) {
                super.attributeChanged(s, attr, oldValue, newValue);
                if("open".equals(attr)) {
                    ev.append("close the world");
                }
            }
        });
        assertFalse("open", d.isOpen());
        assertEquals("closed model", "+", d.getModel());
        d.setOpen(true);
        d.setOpen(true);
        assertTrue("open", d.isOpen());
        assertTrue("vertical", d.isVertical());
        assertEquals("open model", "|", d.getModel());
        assertEquals("events", "close the world", ev.toString());

        d = new Doorway(false);
        assertEquals("closed model", "+", d.getModel());
        d.setOpen(true);
        d.setOpen(true);
        assertEquals("open model", "-", d.getModel());

        assertTrue("isroom", d.isRoom());
        assertFalse("ispassage", d.isPassageway());
        assertEquals("color", "brown", d.getColor());
    }

    public void testBlank() {
        Blank b = new Blank();
        assertEquals("model", " ", b.getModel());
        assertEquals("color", "gray", b.getColor());
        assertEquals("walkable", false, b.isWalkable());
        assertEquals("transparent", false, b.isTransparent());
    }

    public void testNull() {
        Matrix m = new Matrix(20, 20);
        NullNHSpace n = new NullNHSpace(m, 10, 10);
        n.update();
        assertFalse("pickup", n.pickup(null));
        assertFalse("walkable", n.isWalkable());
        assertFalse("transparent", n.isTransparent());
        assertFalse("autopickup", n.isAutopickup());
        n.look(null);
        assertEquals("depth", 0, n.getDepth());
        assertNull("occ", n.getOccupant());
        assertEquals("loot", 0, n.getLoot().size());
        assertEquals("loot2", 0, n.numItems());
        assertEquals("vis", 0, n.visible(null, null, null, Float.MAX_VALUE).size());

        Overlay o = new Overlay(n);
        n.setOverlay(o);
        assertEquals("overlay", o, n.getOverlay());
        o.move(Direction.east);
        assertNull("noverlay", n.getOverlay());
        assertNull("model", n.getModel());
        assertNull("color", n.getColor());
    }
}
