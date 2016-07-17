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


public class TestWear extends junit.framework.TestCase {
    public void testWear() {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        wear(p, new RingMail());
        wear(p, new Fedora());
        wear(p, new Fur_LinedCloak());
    }

    public void testFail() {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        RingMail r = new RingMail();
        wear(p, r);
        p.getInventory().add(new LeatherJacket());
        Wear wear = new Wear();
        wear.setBot(p);
        try {
            wear.perform();
            fail("should have failed");
        }
        catch(ActionCancelledException good) {
        }
        assertTrue("wasn't equipped", p.isEquipped(r));
    }

    private void wear(Patsy p, Item w) {
        Wear wear = new Wear(w);
        wear.setBot(p);
        wear.perform();
        assertTrue("wasn't equipped", p.isEquipped(w));
        Slot[] ss = p.getForm().getSlots(w.getSlotType());
        for(int i=0;i<w.getSlotCount();i++) {
            assertEquals("unoccupied slot "+i, w, ss[i].getOccupant());
        }
    }
}
