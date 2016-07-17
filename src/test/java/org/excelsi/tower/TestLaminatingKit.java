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


public class TestLaminatingKit extends junit.framework.TestCase {
    public void testNothing() {
        Patsy p = new Patsy();
        LaminatingKit k = new LaminatingKit();
        try {
            k.invoke(p);
            fail("did not cancel action");
        }
        catch(ActionCancelledException good) {
        }
    }

    public void testNoMore() {
        Patsy p = new Patsy();
        ScrollOfReanimation s = new ScrollOfReanimation();
        p.getInventory().add(s);
        LaminatingKit k = new LaminatingKit();
        k.setCharges(0);
        k.laminate(s, p);
        k.invoke(p);
        assertFalse("somehow got laminated", s.isLaminated());
    }

    public void testSingle() {
        Patsy p = new Patsy();
        ScrollOfReanimation s = new ScrollOfReanimation();
        p.getInventory().add(s);
        LaminatingKit k = new LaminatingKit();
        k.setStatus(Status.blessed);
        int ch = k.getCharges();
        k.laminate(s, p);
        act(p);
        assertTrue("was not laminated", s.isLaminated());
        assertTrue("did not use any fluid", k.getCharges()<ch);
    }

    public void testMulti() {
        Patsy p = new Patsy();
        ScrollOfReanimation s = new ScrollOfReanimation();
        s.setCount(2);
        p.getInventory().add(s);
        LaminatingKit k = new LaminatingKit();
        int ch = k.getCharges();
        k.invoke(p);
        act(p);
        assertEquals("didn't split: "+p.getInventory(), 2, p.getInventory().size());
        assertTrue("name missing laminated", p.getInventory().getItem()[1].toString().indexOf("laminated")>=0);
        assertTrue("did not use any fluid", k.getCharges()<ch);
    }

    public void testLaminated() {
        Laminated l = new Laminated();
        assertTrue("ident", l.isIdentified());
        assertTrue("class ident", l.isClassIdentified());
        assertEquals("power", 0, l.getPowerModifier());
        assertNotNull("mod", l.getModifier());
        assertEquals("occ", 0, l.getOccurrence());
        l.setIdentified(true);
        l.setClassIdentified(true);
    }

    private void act(Patsy p) {
        int times = 0;
        while(p.isOccupied()) {
            p.tick();
            if(++times==21) {
                fail("took too long");
            }
        }
    }
}
