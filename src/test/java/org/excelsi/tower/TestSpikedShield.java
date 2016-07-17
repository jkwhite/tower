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


import org.excelsi.matrix.Direction;
import org.excelsi.aether.*;


public class TestSpikedShield extends junit.framework.TestCase {
    public void testCounter() throws EquipFailedException {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(10, 10, 6, 6, 20, 20), true);
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        p.setWielded(new Broadsword());
        assertNotNull("did not wield sword", p.getForm().getSlots(SlotType.hand)[0].getOccupant());
        p.wear(new SpikedShield());
        assertNotNull("did not wear shield", p.getForm().getSlots(SlotType.hand)[1].getOccupant());
        BasicBot b = new BasicBot();
        b.setForm(new Humanoid());
        p.setMaxHp(Integer.MAX_VALUE);
        p.setHp(Integer.MAX_VALUE);
        b.setMaxHp(Integer.MAX_VALUE);
        b.setHp(Integer.MAX_VALUE);
        lev.getSpace(10, 10).setOccupant(p);
        lev.getSpace(10, 9).setOccupant(b);
        b.getEnvironment().face(Direction.south);
        final StringBuilder events = new StringBuilder();
        QuantumMechanics m = new QuantumMechanics();
        NHEnvironment.setMechanics(m);
        m.addMechanicsListener(new MechanicsListener() {
            public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
            }

            public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
            }
        });
        int tries = 0;
        for(;;) {
            b.getEnvironment().forward();
            if(b.getHp()<b.getMaxHp()) {
                break;
            }
            if(++tries>10000) {
                fail("that's possible, but pretty unlikely");
            }
        }
    }
}
