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


public class TestWield extends junit.framework.TestCase {
    public void testUnwield() {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        No_dachi n = new No_dachi();
        p.getInventory().add(n);
        Wield w = new Wield();
        w.setBot(p);
        w.perform();
        w = new Wield(null);
        w.setBot(p);
        w.perform();
        assertNull("shouldn't have anything", p.getWielded());
    }

    public void testWieldOk() {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        No_dachi n = new No_dachi();
        p.getInventory().add(n);
        Wield w = new Wield();
        w.setBot(p);
        w.perform();
        assertEquals("didn't wield", n, p.getWielded());
    }

    public void testWieldFail() {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        No_dachi n = new No_dachi();
        n.setStatus(Status.cursed);
        p.getInventory().add(n);
        Wield w = new Wield();
        w.setBot(p);
        w.perform();
        assertEquals("didn't wield", n, p.getWielded());

        Broadsword b = new Broadsword();
        p.getInventory().add(b);
        w = new Wield(b);
        w.setBot(p);
        try {
            w.perform();
            fail("shouldn't succeed");
        }
        catch(ActionCancelledException good) {
        }
        assertEquals("shouldn't be able to wield", n, p.getWielded());
    }

    public void testWieldComestible() {
        BowlOfRamen r = new BowlOfRamen();
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        p.getInventory().add(r);
        Wield a = new Wield(r);
        a.setBot(p);
        a.perform();
        assertEquals("didn't wield", r, p.getWielded());
    }

    public void testFormDisallows() {
        Sling s = new Sling();
        Patsy p = new Patsy();
        p.setForm(new Slime());
        p.getInventory().add(s);
        Wield a = new Wield(s);
        a.setBot(p);
        try {
            a.perform();
            fail("shouldn't succeed");
        }
        catch(ActionCancelledException good) {
        }
        assertNull("shouldn't have anything", p.getWielded());
    }

    public void testTwoHanded() {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        WarMattock m = new WarMattock();
        p.getInventory().add(m);
        Wield w = new Wield(m);
        w.setBot(p);
        w.perform();
        Slot[] ss = p.getForm().getSlots(m.getSlotType());
        for(int i=0;i<m.getSlotCount();i++) {
            assertEquals("unoccupied slot "+i, m, ss[i].getOccupant());
        }
    }

    public void testDualWield() {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        Dagger d1 = new Dagger();
        Dagger d2 = new Dagger();
        // TODO: wield system doesn't support dual-wield yet
    }
}
