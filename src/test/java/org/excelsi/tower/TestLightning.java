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


public class TestLightning extends junit.framework.TestCase {
    public void testLaminant() {
        Lightning f = new Lightning();
        ScrollOfReanimation s = new ScrollOfReanimation();
        s.setLaminated(true);
        Patsy p = new Patsy();
        p.getInventory().add(s);
        Rand.load();
        // default lightning temp is high enough to
        // overcome laminant
        f.inflict(p);
        assertEquals("didn't combust", 0, p.getInventory().size());
        assertTrue("didn't stun", p.getAction() instanceof Stun);
    }

    public void testCombust() {
        Lightning f = new Lightning();
        ScrollOfReanimation s = new ScrollOfReanimation();
        Patsy p = new Patsy();
        p.getInventory().add(s);
        Rand.load();
        f.inflict(p);
        assertEquals("didn't combust", 0, p.getInventory().size());
        assertTrue("didn't stun", p.getAction() instanceof Stun);
    }

    public void testArmor() throws EquipFailedException {
        RingMail m = new RingMail();
        Lightning f = new Lightning(Integer.MAX_VALUE);
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        p.wear(m);
        Rand.load();
        f.inflict(p);
        assertEquals("didn't melt", 0, p.getInventory().size());
        assertTrue("didn't stun", p.getAction() instanceof Stun);
    }

    public void testInWater() {
        Level lev = new Level(10, 10);
        Rand.load();
        lev.addRoom(new Level.Room(0, 0, 8, 8, 8, 8), true);
        new WaterMixin().mix(lev);
        final NHBot p = new BasicBot();
        p.setForm(new Humanoid());
        p.setCommon("p");
        final NHBot p2 = new BasicBot();
        p2.setCommon("p2");
        p2.setForm(new Canid());
        NHBot p3 = new BasicBot();
        p3.setCommon("p3");
        p3.setForm(new Humanoid());
        lev.getSpace(5, 5).setOccupant(p);
        lev.getSpace(3, 3).setOccupant(p2);
        lev.getSpace(1, 1).setOccupant(p3);
        assertTrue("water missing", lev.getSpace(5, 5) instanceof Water);

        // should hit p and p2 but not p3
        final StringBuilder events = new StringBuilder();
        QuantumMechanics q = new QuantumMechanics();
        NHEnvironment.setMechanics(q);
        q.addMechanicsListener(new MechanicsListener() {
            int count = 0;
            public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
            }

            public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
                switch(count) {
                    case 0:
                        if(defender==p) {
                            events.append("jellybean");
                        }
                        else {
                            events.append(defender);
                        }
                        break;
                    case 1:
                        if(defender==p2) {
                            events.append(" drifter");
                        }
                        else {
                            events.append(defender);
                        }
                        break;
                    default:
                        if(defender!=null) {
                            events.append(".");
                        }
                }
                count++;
            }
        });
        new Lightning().inflict(p);
        assertEquals("didn't get events", "jellybean drifter", events.toString());
    }
}
