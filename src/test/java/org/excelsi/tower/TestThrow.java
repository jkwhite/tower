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
import java.util.ArrayList;
import org.excelsi.matrix.*;


public class TestThrow extends junit.framework.TestCase {
    public void testShoot() throws EquipFailedException {
        QuantumMechanics q = new QuantumMechanics();
        NHEnvironment.setMechanics(q);
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        Patsy p = new Patsy();
        p.setStrength(100);
        p.setAgility(100);
        p.setForm(new Humanoid());
        Arrow a = new Arrow();
        a.setCount(8);
        p.getInventory().add(a);
        int amt = a.getCount();
        p.setWielded(new ShortBow());
        BasicBot b = new BasicBot();
        b.setForm(new Slime());
        b.setMaxHp(Integer.MAX_VALUE); // don't want it to die
        b.setHp(Integer.MAX_VALUE);
        Rand.load();

        lev.getSpace(5,5).setOccupant(p);
        lev.getSpace(5,2).setOccupant(b);

        final StringBuilder events = new StringBuilder();
        q.addMechanicsListener(new MechanicsListener() {
            public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
                events.append("dreadful");
            }

            public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
                events.append(" eiri");
            }
        });

        Throw t = new Throw();
        t.setBot(p);
        t.perform();
        assertTrue("didn't decrement", a.getCount()<amt);
        assertEquals("didn't get events", "dreadful eiri", events.toString());

        Dagger d = new Dagger();
        p.getInventory().add(d);
        t = new Throw(d, Direction.north);
        t.setBot(p);
        events.setLength(0);
        t.perform();
        assertFalse("didn't remove from inv", p.getInventory().contains(d));
        assertEquals("didn't get events", "dreadful eiri", events.toString());

        p.setWielded(new Broadsword());
        t = new Throw(a, Direction.north);
        t.setBot(p);
        events.setLength(0);
        t.perform();
        assertTrue("removed from inv", p.getInventory().contains(a));
        assertEquals("didn't get events", "dreadful eiri", events.toString());
    }
}
