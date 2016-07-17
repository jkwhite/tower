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


public class TestLevitating extends junit.framework.TestCase {
    public void testPickup() {
        Patsy p = new Patsy();
        p.addAffliction(new Levitating());
        Floor f = new Floor();
        f.add(new Gold());
        f.setOccupant(p);

        DefaultNHBot.Pickup pick = new DefaultNHBot.Pickup();
        pick.setBot(p);
        pick.perform();
        assertEquals("shouldn't be able to pick up anything", 0, p.getInventory().numItems());
    }

    public void testConsumeOnFloor() {
        Patsy p = new Patsy();
        p.addAffliction(new Levitating());
        Floor f = new Floor();
        f.setOccupant(p);
        Cake c = new Cake();
        f.add(c);
        Consume con = new Consume(c);
        con.setBot(p);
        assertTrue("supposed to be levitating", p.isLevitating());
        final StringBuilder events = new StringBuilder();
        p.addListener(new NHEnvironmentAdapter() {
            public void actionStarted(NHBot b, ProgressiveAction a) {
                super.actionStarted(b, a);
                if(a instanceof Consume.Consuming) {
                    events.append("rockets fall on rocket falls");
                }
            }
        });
        con.perform();
        if(events.length()>0) {
            fail("shouldn't have been able to eat that");
        }
    }

    public void testConsumeOnFloorAuto() {
        Patsy p = new Patsy();
        p.addAffliction(new Levitating());
        Floor f = new Floor();
        f.setOccupant(p);
        Cake c = new Cake();
        f.add(c);
        p.getInventory().add(new Pill(new Strength())); // should choose this
        Consume con = new Consume();
        con.setBot(p);
        assertTrue("supposed to be levitating", p.isLevitating());
        final StringBuilder events = new StringBuilder();
        p.addListener(new NHEnvironmentAdapter() {
            public void actionStarted(NHBot b, ProgressiveAction a) {
                if(a instanceof Consume.Consuming) {
                    Consume.Consuming ing = (Consume.Consuming) a;
                    if(ing.getComestible() instanceof Cake) {
                        events.append("motherfucker==redeemer");
                    }
                }
            }
        });
        con.perform();
        if(events.length()>0) {
            fail("shouldn't have been able to eat that");
        }
    }

    public void testCure() {
        Patsy p = new Patsy();
        Levitating l = new Levitating();
        p.addAffliction(l);
        int times = 0;
        while(l.getRemaining()>0) {
            l.beset();
            l.tick();
            if(++times>1000) {
                fail("shouldn't take that long");
            }
        }
        assertFalse("didn't cure over time", p.isAfflictedBy(Levitating.NAME));
        assertFalse("didn't remove lev flag", p.isLevitating());
    }

    public void testAscend() {
        Patsy p = new Patsy();
        p.addAffliction(new Levitating());
        Level lev = new Level(10, 10);
        Stairs s = new Stairs(true);
        lev.setSpace(s, 2, 2);
        s.setOccupant(p);
        try {
            p.tick();
            fail("did not automatically ascend");
        }
        catch(NullPointerException good) {
            // this is a pretty lame test. NPE is generated
            // when Ascend calls NHEnvironment.ascend
            // which tries to get the Universe, and there is
            // no universe.
        }
    }

    public void testDescend() {
        Patsy p = new Patsy();
        p.addAffliction(new Levitating());
        Level lev = new Level(10, 10);
        Stairs s = new Stairs(false);
        lev.setSpace(s, 2, 2);
        s.setOccupant(p);
        Patsy.Descend a = new Patsy.Descend();
        a.setBot(p);
        p.tick();
        try {
            a.perform();
        }
        catch(NullPointerException bad) {
            // this is a pretty lame test. NPE is generated
            // when Descend calls NHEnvironment.descend
            // which tries to get the Universe, and there is
            // no universe.
            fail("should not be able to descend");
        }
    }
}
