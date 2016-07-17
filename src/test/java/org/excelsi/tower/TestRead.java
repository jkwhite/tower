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


public class TestRead extends junit.framework.TestCase {
    public void testSpecified() {
        Patsy p = new Patsy();
        Diary d = new Diary("red red red red door");
        p.getInventory().add(d);
        Read r = new Read(d);
        r.setBot(p);
        r.perform();
    }

    public void testAuto() {
        Patsy p = new Patsy();
        Diary d = new Diary("big gray cozy house");
        p.getInventory().add(d);
        Read r = new Read();
        r.setBot(p);
        r.perform();
    }

    public void testScroll() throws EquipFailedException {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        Broadsword s = new Broadsword();
        ScrollOfEnchantWeapon w = new ScrollOfEnchantWeapon();
        p.getInventory().add(s);
        p.setWielded(s);
        p.getInventory().add(w);
        Read r = new Read();
        r.setBot(p);
        r.perform();
        assertEquals("didn't get scroll effect", 1, s.getInflictions().size());
    }
}
