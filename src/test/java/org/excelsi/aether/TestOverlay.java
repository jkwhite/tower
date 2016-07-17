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


public class TestOverlay extends junit.framework.TestCase {
    public void testMove() {
        final StringBuffer events = new StringBuffer();
        final StringBuffer events2 = new StringBuffer();
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(10, 10, 6, 6, 20, 20), true);
        NHSpace s = lev.getSpace(10, 10);
        s.addMSpaceListener(new NHSpaceAdapter() {
            public void overlayAdded(NHSpace s, Overlay o) {
                super.overlayAdded(s, o);
                events2.append("you think you're");
            }

            public void overlayRemoved(NHSpace s, Overlay o) {
                super.overlayRemoved(s, o);
                events2.append(" all alone");
            }
        });
        Overlay o = new Overlay(s);
        s.setOverlay(o);
        o.addOverlayListener(new OverlayListener() {
            public void overlayMoved(Overlay o, NHSpace from, NHSpace to) {
                events.append("you sit outside");
            }

            public void overlayRemoved(Overlay o) {
                events.append(" and you wonder if");
            }
        });
        assertEquals("wrong start", s, o.getSpace());
        o.move(Direction.west);
        assertEquals("wrong move", s.move(Direction.west), o.getSpace());
        o.remove();
        assertEquals("events2", "you think you're all alone", events2.toString());
        assertEquals("events", "you sit outside and you wonder if", events.toString());
    }
}
