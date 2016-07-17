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


public class TestMatrixEnvironment extends junit.framework.TestCase {
    public void testMove() {
        Matrix m = new Matrix(7,6);
        TSpace t = new TSpace();
        TSpace u = new TSpace();
        TBot b = new TBot();
        m.setSpace(t,2,2);
        m.setSpace(u,3,2);
        t.setOccupant(b);
        ((MatrixEnvironment)b.getEnvironment()).setMSpace(u);
        assertFalse("didn't unoccupy", t.isOccupied());
        assertEquals("didn't occupy u", b, u.getOccupant());

        b.getEnvironment().move(Direction.east);
        assertEquals("shouldn't be able to move", b, u.getOccupant());
    }

    public void testClosest() {
        Matrix m = new Matrix(7,6);
        TSpace t = new TSpace();
        TBot b = new TBot();
        m.setSpace(t,2,2);
        t.setOccupant(b);
        // TODO: support this?
        try {
            b.getEnvironment().getClosest();
        }
        catch(UnsupportedOperationException e) {
        }
    }

    public void testFaceAway() {
        Matrix m = new Matrix(7,6);
        m.setSpace(new TSpace(), 2,2);
        m.setSpace(new TSpace(), 2,3);
        m.setSpace(new TSpace(), 2,1);
        m.setSpace(new TSpace(), 1,2);
        m.setSpace(new TSpace(), 1,3);
        m.setSpace(new TSpace(), 1,1);
        m.setSpace(new TSpace(), 3,2);
        m.setSpace(new TSpace(), 3,3);
        m.setSpace(new TSpace(), 3,1);
        TBot b1 = new TBot();
        TBot b2 = new TBot();
        m.getSpace(2,2).setOccupant(b1);
        m.getSpace(1,2).setOccupant(b2);
        b2.getEnvironment().faceAway(b1);
        assertEquals("facing", Direction.west, b2.getEnvironment().getFacing());
        m.getSpace(3,2).setOccupant(b2);
        b2.getEnvironment().faceAway(b1);
        assertEquals("facing", Direction.east, b2.getEnvironment().getFacing());
        m.getSpace(2,1).setOccupant(b2);
        b2.getEnvironment().faceAway(b1);
        assertEquals("facing", Direction.north, b2.getEnvironment().getFacing());
        m.getSpace(2,3).setOccupant(b2);
        b2.getEnvironment().faceAway(b1);
        assertEquals("facing", Direction.south, b2.getEnvironment().getFacing());
        m.getSpace(3,3).setOccupant(b2);
        b2.getEnvironment().faceAway(b1);
        assertEquals("facing", Direction.southeast, b2.getEnvironment().getFacing());
        m.getSpace(1,3).setOccupant(b2);
        b2.getEnvironment().faceAway(b1);
        assertEquals("facing", Direction.southwest, b2.getEnvironment().getFacing());
        m.getSpace(1,1).setOccupant(b2);
        b2.getEnvironment().faceAway(b1);
        assertEquals("facing", Direction.northwest, b2.getEnvironment().getFacing());
        m.getSpace(3,1).setOccupant(b2);
        b2.getEnvironment().faceAway(b1);
        assertEquals("facing", Direction.northeast, b2.getEnvironment().getFacing());
    }

    public void testTurnFace() {
        Matrix m = new Matrix(7,6);
        TSpace t = new TSpace();
        TSpace u = new TSpace();
        TBot b1 = new TBot();
        m.setSpace(t,2,2);
        m.setSpace(u,3,2);
        m.setSpace(new TSpace(),1,2);
        m.setSpace(new TSpace(),0,2);
        t.setOccupant(b1);
        EnvironmentAdapter ea = new EnvironmentAdapter();
        try {
            b1.removeListener(ea);
            fail("not listening");
        }
        catch(IllegalArgumentException good) {
        }
        b1.addListener(ea);
        b1.addListener(ea);
        assertEquals("added twice", 1, b1.getListeners().size());
        b1.removeListener(ea);
        b1.addListener(ea);
        TBot b2 = new TBot();
        u.setOccupant(b2);
        b1.getEnvironment().toString();
        b1.getEnvironment().face(t);
        b1.getEnvironment().face(b2);
        assertEquals("wrong facing", Direction.east, b1.getEnvironment().getFacing());
        b1.getEnvironment().faceAway(b2);
        assertEquals("wrong facing", Direction.west, b1.getEnvironment().getFacing());
        b1.getEnvironment().move(b1.getEnvironment().getFacing());
        assertEquals("moved wrong way", m.getSpace(1,2).getOccupant(), b1);
        b1.getEnvironment().forward();
        b1.getEnvironment().backward();
        assertEquals("moved wrong way", m.getSpace(1,2).getOccupant(), b1);
        b1.getEnvironment().backward();
        b1.getEnvironment().backward();
        b1.getEnvironment().turnLeft();
        b1.getEnvironment().turnLeft();
        assertEquals("wrong facing", Direction.south, b1.getEnvironment().getFacing());
        b1.getEnvironment().turnRight();
        b1.getEnvironment().turnRight();
        b1.getEnvironment().turnRight();
        b1.getEnvironment().turnRight();
        assertEquals("wrong facing", Direction.north, b1.getEnvironment().getFacing());
        b2.getEnvironment().approach(b1, 1);
        b2.getEnvironment().approach(b1, 5, false);
        b2.getEnvironment().approach(b1, 5, true);
        b1.getEnvironment().die();
        assertFalse("didn't clear", m.getSpace(2,2).isOccupied());
    }
}
