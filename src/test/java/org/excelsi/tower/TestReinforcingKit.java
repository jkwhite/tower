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


public class TestReinforcingKit extends junit.framework.TestCase {
    public void testNothing() {
        Patsy p = new Patsy();
        ReinforcingKit k = new ReinforcingKit();
        try {
            k.invoke(p);
            fail("did not cancel action");
        }
        catch(ActionCancelledException good) {
        }
    }

    public void testReinforce() {
        final Reinforcable[] tests = new Reinforcable[]{
            new Potion(),
            new Potion(new Strength()),
            new RingMail()
        };
        Status s = Status.blessed;
        for(Reinforcable r:tests) {
            reinforce(r, new BallOfYarn(), s);
            s = s.worse();
        }
    }

    public void testReinforceSplit() {
        Potion p = new Potion(new Lightning());
        p.setCount(2);
        Patsy b = new Patsy();
        b.getInventory().add(p);
        ReinforcingKit r = new ReinforcingKit();
        BallOfYarn y = new BallOfYarn();
        b.getInventory().add(y);
        r.invoke(b);
        int tries = 0;
        while(b.isOccupied()) {
            b.tick();
            if(++tries==50) {
                fail("took too long");
            }
        }
        assertEquals("didn't split: "+b.getInventory(), 2, b.getInventory().size());
        Item i = b.getInventory().getItem()[1];
        assertTrue("name doesn't have reinforced: "+i.toString(), i.toString().indexOf("reinforced")>=0);
    }

    public void testArmor() {
        Patsy p = new Patsy();
        RingMail r = new RingMail();
        BallOfYarn y = new BallOfYarn();
        int pow = r.getModifiedPower();
        new ReinforcingKit().reinforce(r, y, p);
        int tries = 0;
        while(p.isOccupied()) {
            p.tick();
            if(++tries==30) {
                fail("shouldn't take that long");
            }
        }
        assertTrue("didn't affect modified power", pow<r.getModifiedPower());
    }

    public void testCursedArmor() {
        Patsy p = new Patsy();
        RingMail r = new RingMail();
        BallOfYarn y = new BallOfYarn();
        y.setStatus(Status.cursed);
        int pow = r.getModifiedPower();
        new ReinforcingKit().reinforce(r, y, p);
        int tries = 0;
        while(p.isOccupied()) {
            p.tick();
            if(++tries==30) {
                fail("shouldn't take that long");
            }
        }
        assertTrue("didn't affect modified power", pow>r.getModifiedPower());
    }

    private void reinforce(Reinforcable i, ReinforcingMaterial m, Status s) {
        assertFalse("already reinforced", i.isReinforced());
        ReinforcingKit r = new ReinforcingKit();
        r.setStatus(s);
        Patsy p = new Patsy();
        p.getInventory().add((Item)m);
        r.reinforce(i, m, p);
        int tries = 0;
        while(p.isOccupied()) {
            p.tick();
            if(++tries==30) {
                fail("shouldn't take that long");
            }
        }
        assertTrue("wasn't reinforced", i.isReinforced());
        assertTrue("name doesn't have reinforced: "+i.toString(), i.toString().indexOf("reinforced")>=0);
        assertFalse("didn't consume material", p.getInventory().contains((Item)m));

        try {
            i.reinforce(new BallOfYarn());
            fail("rereinforce didn't fail");
        }
        catch(IllegalStateException good) {
        }
    }
}
