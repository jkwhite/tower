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


public class TestFire extends junit.framework.TestCase {
    public void testLaminant() {
        Fire f = new Fire();
        ScrollOfReanimation s = new ScrollOfReanimation();
        s.setLaminated(true);
        Patsy p = new Patsy();
        p.getInventory().add(s);
        Rand.load();
        f.inflict(p);
        assertEquals("combusted", 1, p.getInventory().size());
    }

    public void testCombust() {
        Fire f = new Fire();
        ScrollOfReanimation s = new ScrollOfReanimation();
        Patsy p = new Patsy();
        p.getInventory().add(s);
        Rand.load();
        f.inflict(p);
        assertEquals("didn't combust", 0, p.getInventory().size());
    }

    public void testArmor() throws EquipFailedException {
        RingMail m = new RingMail();
        Fire f = new Fire(Integer.MAX_VALUE);
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        p.wear(m);
        Rand.load();
        f.inflict(p);
        assertEquals("didn't melt", 0, p.getInventory().size());
    }
}
