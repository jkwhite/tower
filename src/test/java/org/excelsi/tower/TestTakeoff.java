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


public class TestTakeoff extends junit.framework.TestCase {
    public void testTakeoff() throws EquipFailedException {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        CloakOfShadows c = new CloakOfShadows();
        p.wear(c);
        Takeoff t = new Takeoff();
        t.setBot(p);
        t.perform();
        assertFalse("should have taken off", p.isEquipped(c));
    }

    public void testNotWearing() {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        CloakOfShadows c = new CloakOfShadows();
        p.getInventory().add(c);
        Takeoff t = new Takeoff(c);
        t.setBot(p);
        try {
            t.perform();
            fail("shouldn't have succeeded");
        }
        catch(ActionCancelledException good) {
        }
        assertFalse("should not be wearing", p.isEquipped(c));
    }

    public void testCursed() throws EquipFailedException {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        CloakOfShadows c = new CloakOfShadows();
        c.setStatus(Status.cursed);
        p.getInventory().add(c);
        p.wear(c);
        Takeoff t = new Takeoff(c);
        t.setBot(p);
        try {
            t.perform();
            fail("shouldn't have succeeded");
        }
        catch(ActionCancelledException good) {
        }
        assertTrue("should be wearing", p.isEquipped(c));
    }
}
