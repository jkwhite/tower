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


public class TestConfusion extends junit.framework.TestCase {
    public void testAfflict() {
        Patsy p = new Patsy();
        Potion potion = new Potion(new Confusion());
        Quaff q = new Quaff(potion);
        q.setBot(p);
        q.perform();
        assertTrue("didn't afflict", p.isAfflictedBy(Confused.NAME));
    }

    public void testCursed() {
        Patsy p = new Patsy();
        Potion potion = new Potion(new Confusion());
        potion.setStatus(Status.cursed);
        Quaff q = new Quaff(potion);
        q.setBot(p);
        q.perform();
        assertTrue("didn't afflict", p.isAfflictedBy(Confused.NAME));
    }

    public void testBlessed() {
        Patsy p = new Patsy();
        Potion potion = new Potion(new Confusion());
        potion.setStatus(Status.blessed);
        Quaff q = new Quaff(potion);
        q.setBot(p);
        q.perform();
        assertTrue("didn't afflict", p.isAfflictedBy(Confused.NAME));
    }

    public void testTwice() {
        Patsy p = new Patsy();
        new Confusion().inflict(p);
        new Confusion().inflict(p);
        assertTrue("didn't afflict", p.isAfflictedBy(Confused.NAME));
    }
}
