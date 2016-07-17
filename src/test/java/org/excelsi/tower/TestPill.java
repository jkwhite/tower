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


public class TestPill extends junit.framework.TestCase {
    public void testNaming() {
        Pill p = new Pill();
        assertEquals("wrong empty name", "an empty capsule", p.toString());
        Cyanide c = new Cyanide();
        c.setClassIdentified(false);
        p.addFragment(c);
        assertFalse("should not be ident", c.isClassIdentified());
        assertTrue("wrong cyanide name: "+p.toString(), p.toString().endsWith(c.getColor()+" pill"));
        p.setIdentified(true);
        assertEquals("wrong ident name", "an uncursed "+c.getColor()+" pill", p.toString());
        p.setClassIdentified(true);
        c.setClassIdentified(true);
        assertEquals("wrong class ident name", "an uncursed cyanide pill", p.toString());
    }

    public void testConsume() {
        NHBot b = new ItemBot();
        b.setForm(new Humanoid());
        Pill p = new Pill();
        p.addFragment(new Cyanide());
        b.getInventory().add(p);

        Consume q = new Consume(p);
        q.setBot(b);
        q.perform();
        assertEquals("wrong name", "a cyanide pill", p.toString());
        assertEquals("wrong count", 1, p.getCount());
        assertFalse("did not remove from inv", b.getInventory().contains(p));
        assertTrue("did not kill", b.isDead());
        assertTrue("did not class ident pill", p.isClassIdentified());
        assertTrue("did not class ident frag", new Cyanide().isClassIdentified());
    }

    public void testConsumeSeveral() {
        NHBot b = new ItemBot();
        b.setForm(new Humanoid());
        Pill p = new Pill();
        p.addFragment(new Cyanide());
        p.setCount(3);
        b.getInventory().setKeyed(true);
        b.getInventory().add(p);

        for(int i=0;i<3;i++) {
            Consume q = new Consume(p);
            q.setBot(b);
            q.perform();
            if(i<2) {
                assertEquals("wrong num of items at "+i+": "+b.getInventory(), 1, b.getInventory().size());
                assertEquals("wrong count at "+i+": "+b.getInventory(), 2-i, b.getInventory().getItem()[0].getCount());
                assertTrue("removed from inv at "+i, b.getInventory().contains(p));
            }
            else {
                assertEquals("did not remove final from inv", 0, b.getInventory().size());
            }
            assertTrue("did not kill", b.isDead());
            b.setDead(false);
        }
    }

    public void testWeight() {
        assertTrue("empty pill does not weight less than full", new Pill().getWeight()<new Pill(new WaterInfliction()).getWeight());
    }

    public void testSize() {
        assertTrue("pill contents should not afect size", new Pill().getSize()==new Pill(new WaterInfliction()).getSize());
    }

    public void testEquals() {
        Pill p1 = new Pill();
        Pill p2 = new Pill(new Levitation());
        assertFalse("should not equal", p1.equals(p2));
        p1.addFragment(new Levitation());
        assertEquals("should equal", p1, p2);
    }
}
