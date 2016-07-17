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
import org.excelsi.matrix.MSpace;


public class TestApply extends junit.framework.TestCase {
    public void testFabricator() {
        Fabricator fab = new Fabricator();
        Patsy p = new Patsy();
        fab.setOccupant(p);
        Apply a = new Apply();
        a.setBot(p);
        a.perform();
    }

    public void testBattery() {
        Patsy p = new Patsy();
        WandOfCold c = new WandOfCold();
        c.setCharges(0);
        Battery b = new Battery();
        p.getInventory().add(c);
        p.getInventory().add(b);
        Apply a = new Apply();
        a.setBot(p);
        a.perform();
        assertTrue("didn't charge", c.getCharges()>0);
    }

    public void testDestruct() {
        QuantumMechanics q = new QuantumMechanics();
        NHEnvironment.setMechanics(q);
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        p.getInventory().add(new NuclearBomb());
        lev.getSpace(5,5).setOccupant(p);
        BasicBot b = new BasicBot();
        b.setForm(new Reptile());
        lev.getSpace(3,3).setOccupant(b);
        final StringBuilder events = new StringBuilder();
        q.addMechanicsListener(new MechanicsListener() {
            public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
                events.append("blossom ");
            }

            public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
                if(defender!=null) {
                    events.append("and blood");
                }
            }
        });
        Apply a = new Apply();
        a.setBot(p);
        a.perform();
        assertFalse("shouldn't harm invoker", p.isDead());
        assertEquals("didn't get events", "blossom and blood", events.toString());
    }
}
