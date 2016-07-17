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
import org.excelsi.matrix.Direction;


public class TestEye extends junit.framework.TestCase {
    public void testFreeze() throws EquipFailedException {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        BasicBot b = new BasicBot();
        b.setForm(new Eye());
        Patsy p = new Patsy();
        p.setForm(new Humanoid());

        NHEnvironment.setMechanics(new QuantumMechanics());

        lev.getSpace(5,5).setOccupant(p);
        lev.getSpace(4,5).setOccupant(b);
        p.setWielded(new Broadsword());
        Rand.load();
        p.getEnvironment().face(Direction.west);
        p.getEnvironment().forward();

        assertTrue("didn't freeze", p.getAction() instanceof Frozen);
        int tries = 0;
        while(p.isOccupied()) {
            if(++tries==1000) {
                fail("shouldn't take that long");
            }
            p.tick();
        }
    }
}
