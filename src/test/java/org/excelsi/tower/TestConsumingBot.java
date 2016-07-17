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


public class TestConsumingBot extends junit.framework.TestCase {
    public void testIntercept() {
        NHEnvironment.setMechanics(new QuantumMechanics());
        ConsumingBot b = new ConsumingBot();
        b.setForm(new Canid());
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        lev.getSpace(5, 5).setOccupant(b);
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        lev.getSpace(6,5).setOccupant(p);
        LegOfMutton leg = new LegOfMutton();
        p.getInventory().add(leg);
        Throw t = new Throw(leg, Direction.west);
        t.setBot(p);
        t.perform();
        assertTrue("didn't start eating", b.getAction() instanceof Consume.Consuming);
    }

    public void testEat() {
        NHEnvironment.setMechanics(new QuantumMechanics());
        ConsumingBot b = new ConsumingBot();
        b.setCommon("blot");
        b.setForm(new Canid());
        b.setQuickness(90);
        Level lev = new Level(20, 20);
        lev.setSpace(new Floor(), 19,19);
        lev.getSpace(19,19).setOccupant(new Patsy()); // or infinite loop
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        lev.getSpace(5, 5).setOccupant(b);
        Cake c = new Cake();
        lev.getSpace(6,5).add(c);
        int times = 0;
        while(!b.isOccupied()) {
            if(++times>200) {
                fail("should have acted by now");
            }
            lev.tick();
        }
    }

    public void testChase() {
        NHEnvironment.setMechanics(new QuantumMechanics());
        ConsumingBot b = new ConsumingBot();
        b.setTemperament(Temperament.hungry);
        b.setCommon("blot");
        b.setForm(new Canid());
        b.setQuickness(90);
        Level lev = new Level(20, 20);
        lev.setSpace(new Floor(), 19,19);
        lev.getSpace(19,19).setOccupant(new Patsy()); // or infinite loop
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        lev.getSpace(5, 5).setOccupant(b);
        Cake c = new Cake();
        lev.getSpace(6,5).add(c);
        int times = 0;
        while(!b.isOccupied()) {
            if(++times>200) {
                fail("should have acted by now");
            }
            lev.tick();
        }
    }

    public void testChallenge() {
        QuantumMechanics q = new QuantumMechanics();
        NHEnvironment.setMechanics(q);
        ConsumingBot b = new ConsumingBot();
        b.setTemperament(Temperament.hungry);
        b.setCommon("blot");
        b.setForm(new Canid());
        b.setQuickness(90);
        Level lev = new Level(20, 20);
        lev.setSpace(new Floor(), 19,19);
        lev.getSpace(19,19).setOccupant(new Patsy()); // or infinite loop
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        lev.getSpace(5, 5).setOccupant(b);
        ConsumingBot b2 = new ConsumingBot();
        b2.setTemperament(Temperament.hungry);
        b2.setCommon("blot");
        b2.setForm(new Canid());
        b2.setQuickness(90);
        b2.setThreat(b, Threat.friendly);
        b.setThreat(b2, Threat.friendly);
        lev.getSpace(3, 5).setOccupant(b2);
        Cake c = new Cake();
        lev.getSpace(6,5).add(c);
        final StringBuilder events = new StringBuilder();
        q.addMechanicsListener(new MechanicsListener() {
            public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
                events.append("i don't feel and that feels fine");
            }

            public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
            }
        });

        int times = 0;
        while(events.length()==0) {
            if(++times>200) {
                fail("should have acted by now");
            }
            lev.tick();
        }
    }
}
