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


public class TestLevitation extends junit.framework.TestCase {
    public void testInvert() {
        Patsy p = new Patsy();
        Potion potion = new Potion(new Levitation());
        Quaff q = new Quaff(potion);
        q.setBot(p);
        q.perform();
        assertTrue("didn't afflict", p.isAfflictedBy(Levitating.NAME));
        Potion potion2 = new Potion(new Levitation());
        p.getInventory().add(potion2);
        q = new Quaff(potion2);
        q.setBot(p);
        q.perform();
        assertFalse("didn't cure", p.isAfflictedBy(Levitating.NAME));
    }

    public void testAfflict() {
        Patsy p = new Patsy();
        Potion potion = new Potion(new Levitation());
        Quaff q = new Quaff(potion);
        q.setBot(p);
        q.perform();
        assertTrue("didn't afflict", p.isAfflictedBy(Levitating.NAME));
    }

    public void testCursedAfflict() {
        Patsy p = new Patsy();
        Potion potion = new Potion(new Levitation());
        potion.setStatus(Status.cursed);
        Quaff q = new Quaff(potion);
        q.setBot(p);
        q.perform();
        assertTrue("didn't afflict", p.isAfflictedBy(Levitating.NAME));
    }

    public void testBlessedAfflict() {
        Patsy p = new Patsy();
        Potion potion = new Potion(new Levitation());
        potion.setStatus(Status.blessed);
        Quaff q = new Quaff(potion);
        q.setBot(p);
        q.perform();
        assertTrue("didn't afflict", p.isAfflictedBy(Levitating.NAME));
    }
}
