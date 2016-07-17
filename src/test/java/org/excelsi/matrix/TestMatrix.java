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


import java.util.ArrayList;
import java.util.Arrays;


public class TestMatrix extends junit.framework.TestCase {
    public void testSize() {
        Matrix m = new Matrix(7, 6);
        assertEquals("wrong width", 7, m.width());
        assertEquals("wrong height", 6, m.height());
        assertEquals("wrong size", 7*6, m.size());
        assertEquals("wrong str", "M7x6", m.toString());
        TSpace ms = new TSpace();
        m.setSpace(ms, 6, 5);
        assertEquals("space changed", ms, m.getSpace(6, 5));
        try {
            m.setSpace(new TSpace(), 7, 6);
            fail("out of bounds");
        }
        catch(IndexOutOfBoundsException good) {
        }
    }

    public void testReplace() {
        Matrix m = new Matrix(7, 6);
        TSpace waxing = new TSpace();
        TSpace waning = new TSpace();
        m.setSpace(waning, 3, 3);
        assertEquals("waxing wrong", waxing, waning.replace(waxing));
        assertEquals("replace wrong", waxing, m.getSpace(3, 3));
    }

    public void testSurrounding() {
        Matrix m = new Matrix(20, 20);
        for(int i=0;i<10;i++) {
            for(int j=0;j<10;j++) {
                m.setSpace(new TSpace(), i, j);
            }
        }
        MSpace[] sur = m.getSpace(1, 2).surrounding();
        assertEquals("wrong length", 8, sur.length);
        for(int i=0;i<sur.length;i++) {
            MatrixMSpace mms = (MatrixMSpace) sur[i];
            assertNotNull("got null at "+i, sur[i]);
            assertTrue("illegal x: "+mms.getI(), mms.getI()>=0&&mms.getI()<=2);
            assertTrue("illegal y: "+mms.getJ(), mms.getJ()>=1&&mms.getJ()<=3);
        }
        sur = m.getSpace(1, 1).surrounding();
        assertEquals("wrong length", 8, sur.length);
        for(int i=0;i<sur.length;i++) {
            MatrixMSpace mms = (MatrixMSpace) sur[i];
            assertNotNull("got null at "+i, sur[i]);
            assertTrue("illegal x: "+mms.getI(), mms.getI()>=0&&mms.getI()<=2);
            assertTrue("illegal y: "+mms.getJ(), mms.getJ()>=0&&mms.getJ()<=2);
        }
    }

    public void testReplaceWithOccupant() {
        final StringBuilder events = new StringBuilder();
        Matrix m = new Matrix(7, 6);
        TSpace waning = new TSpace();
        TSpace waxing = new TSpace();
        waning.addMSpaceListener(new MSpaceListener() {
            public void occupied(MSpace s, Bot b) {
                events.append("the floating pyramid");
            }

            public void unoccupied(MSpace s, Bot b) {
                events.append(" over frankfurt");
            }

            public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
                events.append("-");
            }
        });
        waxing.addMSpaceListener(new MSpaceListener() {
            public void occupied(MSpace s, Bot b) {
                events.append(" that the taxi driver saw");
            }

            public void unoccupied(MSpace s, Bot b) {
                events.append(" when he was landing");
            }

            public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
                events.append("-");
            }
        });
        m.setSpace(waning, 3, 3);
        TBot t = new TBot();
        waning.setOccupant(t);
        assertEquals("waxing wrong", waxing, waning.replace(waxing));
        assertEquals("replace wrong", waxing, m.getSpace(3, 3));
        assertEquals("occupant missing", t, waxing.getOccupant());
        waxing.clearOccupant();
        assertEquals("missed events",
            "the floating pyramid over frankfurt that the taxi driver saw when he was landing",
            events.toString());
    }

    public void testDistance() {
        Matrix m = new Matrix(7, 6);
        m.setSpace(new TSpace(), 2, 2);
        m.setSpace(new TSpace(), 4, 4);
        assertEquals("wrong distance", (float) Math.sqrt(8), m.getSpace(2,2).distance(m.getSpace(4,4)));
        assertEquals("wrong distance", (float) Math.sqrt(8), m.getSpace(4,4).distance(m.getSpace(2,2)));
    }

    public void testListener() {
        final ArrayList<MSpace> added = new ArrayList<MSpace>();
        final ArrayList<MSpace> removed = new ArrayList<MSpace>();

        Matrix m = new Matrix(7, 6);
        MatrixListener listener = new MatrixListener() {
            public void spacesRemoved(Matrix m, MSpace[] spaces) {
                removed.addAll(Arrays.asList(spaces));
            }

            public void spacesAdded(Matrix m, MSpace[] spaces) {
                added.addAll(Arrays.asList(spaces));
            }

            public void attributeChanged(Matrix m, String attr, Object o, Object n) {
            }
        };
        try {
            m.removeListener(listener);
            fail("illegal");
        }
        catch(IllegalArgumentException good) {
        }
        m.addListener(listener);
        m.addListener(listener);
        TSpace[] mss = new TSpace[]{new TSpace(), new TSpace(), new TSpace()};
        for(int i=0;i<mss.length;i++) {
            m.setSpace(mss[i], i, i);
        }
        for(int i=0;i<mss.length;i++) {
            m.setSpace(null, i, i);
        }
        for(int i=0;i<mss.length;i++) {
            assertEquals("wrong added at "+i, mss[i], added.get(i));
            assertEquals("wrong removed at "+i, mss[i], removed.get(i));
        }
        m.removeListener(listener);
    }

    static class TSpace extends MatrixMSpace {
        public boolean isTransparent() {
            return true;
        }

        public boolean isWalkable() {
            return true;
        }

        public void update() {
        }
    }

    static class TBot extends DefaultBot {
        public float sanity() {
            return 1;
        }

        public void act() {
        }
    }
}
