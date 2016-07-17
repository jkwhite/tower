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


import org.excelsi.matrix.*;


public class TestEventQueue extends junit.framework.TestCase {
    public void testReplay() {
        final StringBuffer events = new StringBuffer();
        Matrix m = new Matrix(20, 20);
        EventQueue.getEventQueue().addMatrixListener(m, new EverythingAdapter() {
            public void spacesRemoved(Matrix m, MSpace[] spaces) {
                super.spacesRemoved(m, spaces);
                events.append("echoes of the night");
            }

            public void spacesAdded(Matrix m, MSpace[] spaces) {
                super.spacesAdded(m, spaces);
                events.append("out from the ");
            }
        });
        m.setSpace(new Floor(), 8, 8);
        m.setSpace(null, 8, 8);
        EventQueue.getEventQueue().play();
        assertEquals("didn't get events", "out from the echoes of the night", events.toString());
    }
}
