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


import static org.excelsi.matrix.TestMatrix.TSpace;
import static org.excelsi.matrix.TestMatrix.TBot;
import java.util.EnumSet;


public class TestMatrixMSpace extends junit.framework.TestCase {
    public void testClone() {
        TSpace t = new TSpace();
        TSpace clone = (TSpace) t.clone();
    }

    public void testListener() {
        TSpace t = new TSpace();
        t.removeMSpaceListener(null);
        t.getMSpaceListeners();
        MSpaceAdapter m = new MSpaceAdapter();
        t.addMSpaceListener(m);
        t.addMSpaceListener(m);
        t.removeMSpaceListener(m);
        t.getMSpaceListeners();
        t.removeMSpaceListener(m);
    }

    public void testAttributes() {
        TSpace t = new TSpace();
        assertFalse("shouldn't be stretchy", t.isStretchy());
        t.trigger();
    }

    public void testOccupy() {
        TSpace t = new TSpace();
        try {
            t.setOccupant(null);
            fail("shouldn't allow null occ");
        }
        catch(IllegalArgumentException good) {
        }
        TBot b = new TBot();
        t.setOccupant(b);
        try {
            t.setOccupant(new TBot());
            fail("shouldn't override existing occ");
        }
        catch(IllegalStateException good) {
        }
    }

    public void testMoveOccupant() {
        TSpace t1 = new TSpace();
        TSpace t2 = new TSpace();
        try {
            t1.moveOccupant(t2);
            fail("should be illegal");
        }
        catch(IllegalArgumentException good) {
        }
        TBot b = new TBot();
        t1.setOccupant(b);
        t1.moveOccupant(t2);
        assertEquals("wrong dest occ", b, t2.getOccupant());
        assertFalse("didn't move", t1.isOccupied());
        t1.setOccupant(new TBot());
        try {
            t2.moveOccupant(t1);
            fail("overwrote b2");
        }
        catch(IllegalStateException good) {
        }
    }

    public void testMoveListen() {
        final TSpace t1 = new TSpace();
        final TSpace t2 = new TSpace();
        final TBot b = new TBot();
        final StringBuilder events = new StringBuilder();
        t1.addMSpaceListener(new MSpaceAdapter() {
            public void moved(MSpace source, MSpace from, MSpace to, Bot bot) {
                super.moved(source, from, to, bot);
                if(source==t1&&from==t1&&to==t2&&bot==b) {
                    events.append("there is no human so techno");
                }
            }
        });
        t1.setOccupant(b);
        t1.moveOccupant(t2);
        assertEquals("wrong dest occ", b, t2.getOccupant());
        assertFalse("didn't move", t1.isOccupied());
        assertTrue("didn't get event", events.length()>0);
    }

    public void testClear() {
        final TSpace t = new TSpace();
        final TBot b = new TBot();
        try {
            t.clearOccupant();
            fail("illegal");
        }
        catch(IllegalStateException good) {
        }
        t.setOccupant(b);
        final StringBuilder events = new StringBuilder();
        t.addMSpaceListener(new MSpaceAdapter() {
            public void unoccupied(MSpace s, Bot bot) {
                super.unoccupied(s, bot);
                if(b==bot) {
                    events.append("there is nobody so day-glo");
                }
            }
        });
        t.clearOccupant();
        assertFalse("should not be occupied", t.isOccupied());
        assertTrue("didn't get event", events.length()>0);
    }

    public void testMoveSurround() {
        Matrix m = new Matrix(7,6);
        TSpace t1 = new TSpace();
        TSpace t2 = new TSpace();
        m.setSpace(t1, 3, 3);
        m.setSpace(t2, 3, 4);
        assertEquals("wrong layout", t2, t1.move(Direction.south));
        int c = 0;
        for(MSpace s:t1.surrounding()) {
            if(s!=null) {
                c++;
                if(s!=t2) {
                    fail("somehow got space "+s);
                }
            }
        }
        assertEquals("somehow got more than 1 nonnull space", 1, c);
        c = 0;
        for(MSpace s:t1.cardinal()) {
            if(s!=null) {
                c++;
                if(s!=t2) {
                    fail("somehow got space "+s);
                }
            }
        }
        c = 0;
        int tot = 0;
        for(MSpace s:t1.surrounding(EnumSet.of(Direction.north, Direction.south))) {
            tot++;
            if(s!=null) {
                c++;
            }
        }
        assertEquals("didn't get north/south", 2, tot);
        assertEquals("somehow got more than 1 nonnull space", 1, c);
        for(MSpace s:t1.surrounding(true)) {
            if(s==null) {
                fail("got null on nonnull surround");
            }
            if(s instanceof NullMatrixMSpace) {
                assertFalse(s.isWalkable());
                assertFalse(s.isOccupied());
                assertNull(s.getOccupant());
                assertFalse(s.isTransparent());
                s.update();
                try {
                    s.clearOccupant();
                    fail("clearOccupant");
                }
                catch(IllegalStateException good) {
                }
                try {
                    s.setOccupant(new TBot());
                    fail("setOccupant");
                }
                catch(IllegalStateException good) {
                }
            }
        }
        for(MSpace s:t1.cardinal(true)) {
            if(s==null) {
                fail("got null on nonnull surround");
            }
        }
    }

    public void testDirectionTo() {
        Matrix m = new Matrix(7,6);
        TSpace t1 = new TSpace();
        TSpace t2 = new TSpace();
        m.setSpace(t1, 3, 3);
        m.setSpace(t2, 3, 4);
        assertEquals("wrong neighbor dir", Direction.south, t1.directionTo(t2));

        TSpace t3 = new TSpace();
        m.setSpace(t3, 0, 0);
        assertEquals("wrong foreign dir", Direction.northwest, t1.directionTo(t3));
        assertEquals("wrong foreign dir", Direction.southeast, t3.directionTo(t1));

        TSpace t4 = new TSpace();
        m.setSpace(t4, 4, 0);
        assertEquals("wrong foreign dir", Direction.north, t2.directionTo(t4));
        assertEquals("wrong foreign dir", Direction.south, t4.directionTo(t2));
        assertEquals("wrong foreign dir", Direction.west, t4.directionTo(t3));
        assertEquals("wrong foreign dir", Direction.east, t3.directionTo(t4));
    }

    public void testCreator() {
        Matrix m = new Matrix(7,6);
        TSpace t1 = new TSpace();
        TSpace t2 = new TSpace();
        TSpace t3 = new TSpace();
        m.setSpace(t1, 3, 3);
        MSpace c = t1.creator();
        c = c.move(Direction.east).move(Direction.east);
        assertFalse("walkable", c.isWalkable());
        assertFalse("transparent", c.isTransparent());
        c.replace(t2);
        assertEquals("didn't create replacement", t2, m.getSpace(5,3));
        c = c.move(Direction.west).move(Direction.west);
        assertTrue("walkable", c.isWalkable());
        assertTrue("transparent", c.isTransparent());
        c.replace(t3);
        c.update();
        assertEquals("didn't create replacement", t3, m.getSpace(3,3));
    }

    public void testSpaces() {
        Matrix m = new Matrix(7,6);
        TSpace t1 = new TSpace();
        m.setSpace(t1, 3, 3);
        assertEquals("wrong count: "+t1.spaces().length, m.width()*m.height(), t1.spaces().length);
    }
}
