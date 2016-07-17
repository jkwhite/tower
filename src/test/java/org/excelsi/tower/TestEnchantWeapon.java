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


public class TestEnchantWeapon extends junit.framework.TestCase {
    public void testEmptyHanded() {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        ScrollOfEnchantWeapon w = new ScrollOfEnchantWeapon();
        w.setStatus(Status.blessed);
        w.invoke(p);
    }

    public void testBlessed() throws EquipFailedException {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        Broadsword b = new Broadsword();
        p.setWielded(b);
        ScrollOfEnchantWeapon w = new ScrollOfEnchantWeapon();
        w.setStatus(Status.blessed);
        w.invoke(p);
        assertTrue("didn't enchant", b.getFragments().get(0) instanceof Fire);
    }

    public void testUncursed() throws EquipFailedException {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        Broadsword b = new Broadsword();
        p.setWielded(b);
        ScrollOfEnchantWeapon w = new ScrollOfEnchantWeapon();
        w.invoke(p);
        assertTrue("didn't enchant", b.getFragments().get(0) instanceof Fire);
    }

    public void testCursed() throws EquipFailedException {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        Broadsword b = new Broadsword();
        p.setWielded(b);
        p.getInventory().add(new Diary("i stare out to sea at the ships at night"));
        ScrollOfEnchantWeapon w = new ScrollOfEnchantWeapon();
        w.setStatus(Status.cursed);
        Rand.load();
        w.invoke(p);
        assertEquals("didn't combust", 1, p.getInventory().size());
        assertTrue("combusted wrong item", p.getInventory().getItem()[0] instanceof Broadsword);
    }
}
