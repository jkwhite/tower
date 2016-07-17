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


public class TestCold extends junit.framework.TestCase {
    protected void setUp() {
        Rand.load();
    }

    public void testReinforcement() {
        Patsy p = new Patsy();
        Potion potion = new Potion();
        potion.reinforce(new BallOfYarn());
        p.getInventory().add(potion);
        Cold c = new Cold();
        c.inflict(p);
        assertEquals("shattered", 1, p.getInventory().size());
    }

    public void testShatterSeveral() {
        Patsy p = new Patsy();
        Potion potions = new Potion(new Confusion());
        potions.setCount(5);
        p.getInventory().add(potions);
        Cold c = new Cold();
        c.inflict(p);
        assertEquals("lost potions", 1, p.getInventory().size());
        assertEquals("did not shatter", 4, p.getInventory().getItem()[0].getCount());
    }

    public void testShatter() {
        Patsy p = new Patsy();
        p.getInventory().add(new Potion(new Confusion()));
        Cold c = new Cold();
        c.inflict(p);
        assertEquals("did not shatter", 0, p.getInventory().size());
    }

    public void testEmpty() {
        Patsy p = new Patsy();
        p.getInventory().add(new Potion());
        Cold c = new Cold();
        c.inflict(p);
        assertEquals("shattered", 1, p.getInventory().size());
    }
}
