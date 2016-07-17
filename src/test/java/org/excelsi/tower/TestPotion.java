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


public class TestPotion extends junit.framework.TestCase {
    public void testImmerseEmpty() {
        Potion p = new Potion();
        Item b = new Broadsword();
        Patsy bot = new Patsy();
        p.immerse(bot, b);
        assertEquals("shouldn't have any fragments", 0, b.getFragments().size());
    }

    public void testImmerseSingle() {
        Potion p = new Potion(new Cyanide());
        Item b = new Broadsword();
        Patsy bot = new Patsy();
        bot.getInventory().setKeyed(true);
        bot.getInventory().add(p);
        bot.getInventory().add(b);
        String key = bot.getInventory().keyFor(p);
        p.immerse(bot, b);
        assertTrue("no cyanide", b.getFragments().get(0) instanceof Cyanide);
        assertEquals("key changed", key, bot.getInventory().keyFor(p));
    }

    public void testImmerseMulti() {
        Potion p = new Potion(new Cyanide());
        p.setCount(5);
        Item b = new Broadsword();
        Patsy bot = new Patsy();
        bot.getInventory().setKeyed(true);
        bot.getInventory().add(p);
        bot.getInventory().add(b);
        String key = bot.getInventory().keyFor(p);
        p.immerse(bot, b);
        assertTrue("no cyanide", b.getFragments().get(0) instanceof Cyanide);
        assertEquals("key changed", key, bot.getInventory().keyFor(p));
        assertEquals("wrong item count", 3, bot.getInventory().size());
    }

    public void testNaming() {
        Potion p = new Potion();
        assertEquals("wrong empty name", "an empty potion", p.toString());
        Cyanide c = new Cyanide();
        c.setClassIdentified(false);
        p.addFragment(c);
        assertTrue("wrong cyanide name", p.toString().endsWith(c.getColor()+" potion"));
        p.setIdentified(true);
        assertEquals("wrong ident name", "an uncursed "+c.getColor()+" potion", p.toString());
        p.setClassIdentified(true);
        c.setClassIdentified(true);
        assertEquals("wrong class ident name", "an uncursed potion of cyanide", p.toString());
    }

    public void testConsume() {
        NHBot b = new ItemBot();
        b.setForm(new Humanoid());
        Potion p = new Potion();
        p.addFragment(new Cyanide());
        b.getInventory().add(p);

        Quaff q = new Quaff(p);
        q.setBot(b);
        q.perform();
        assertEquals("wrong name", "an empty potion", p.toString());
        assertEquals("wrong count", 1, p.getCount());
        assertTrue("removed from inv", b.getInventory().contains(p));
        assertTrue("did not kill", b.isDead());
        assertTrue("did not class ident potion", p.isClassIdentified());
        assertTrue("did not class ident frag", new Cyanide().isClassIdentified());
    }

    public void testConsumeSeveral() {
        NHBot b = new Patsy();
        b.setForm(new Humanoid());
        Potion p = new Potion();
        p.addFragment(new Cyanide());
        p.setCount(3);
        b.getInventory().setKeyed(true);
        b.getInventory().add(p);

        Potion e = null;
        String ek = null;
        for(int i=0;i<3;i++) {
            Quaff q = new Quaff();
            q.setBot(b);
            q.perform();
            if(i<2) {
                assertEquals("wrong num of items at "+i+": "+b.getInventory(), 2, b.getInventory().size());
            }
            else {
                assertEquals("wrong num of items at "+i+": "+b.getInventory(), 1, b.getInventory().size());
            }
            if(i==0) {
                e = (Potion) b.getInventory().getItem()[1];
                ek = b.getInventory().keyFor(e);
                assertEquals("wrong name at "+i, "an empty potion", e.toString());
            }
            else {
                assertEquals("wrong name at "+i, (i+1)+" empty potions", e.toString());
                assertEquals("e key changed: "+b.getInventory(), ek, b.getInventory().keyFor(e));
            }
            if(i<2) {
                assertEquals("wrong count at "+i+": "+b.getInventory(), 2-i, b.getInventory().getItem()[0].getCount());
                assertTrue("removed from inv at "+i, b.getInventory().contains(p));
                assertEquals("removed from inv at "+i, 2, b.getInventory().size());
            }
            else {
                assertEquals("did not remove final from inv", 1, b.getInventory().size());
            }
            assertTrue("did not kill", b.isDead());
            b.setDead(false);
        }
    }

    public void testHealing() {
        NHBot b = new ItemBot();
        b.setForm(new Humanoid());
        Potion p = new Potion();
        p.addFragment(new Healing());
        p.setCount(3);
        b.getInventory().setKeyed(true);
        b.getInventory().add(p);
        Potion em = new Potion();
        em.setCount(3);
        b.getInventory().add(em);
        for(int i=0;i<3;i++) {
            Quaff q = new Quaff((Potion)b.getInventory().getItem()[0]);
            q.setBot(b);
            q.perform();
        }
        assertEquals("wrong num of items", 1, b.getInventory().size());
        Potion e = (Potion) b.getInventory().getItem()[0];
        assertTrue("potion is not empty: "+e, e.isEmpty());
        assertEquals("wrong empty count", 6, e.getCount());
    }

    public void testPoison() {
        Potion p = new Potion(new Poison());
        new Poison().setClassIdentified(false);
        assertTrue("wrong unident name", p.toString().endsWith(new Poison().getColor()+" potion"));
        p.setClassIdentified(true);
        assertEquals("wrong name", "a potion of poison", p.toString());
    }

    public void testWeight() {
        assertTrue("empty potion does not weight less than full", new Potion().getWeight()<new Potion(new WaterInfliction()).getWeight());
    }

    public void testSize() {
        assertTrue("potion contents should not afect size", new Potion().getSize()==new Potion(new WaterInfliction()).getSize());
    }

    public void testEquals() {
        Potion p1 = new Potion();
        Potion p2 = new Potion(new Levitation());
        assertFalse("should not equal", p1.equals(p2));
        p1.addFragment(new Levitation());
        assertEquals("should equal", p1, p2);
        p1.reinforce(new BallOfYarn());
        assertFalse("should not equal", p1.equals(p2));
    }
}
