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


public class TestHunger extends junit.framework.TestCase {
    public void testStarvation() {
        Patsy p = new Patsy();
        Hunger h = new Hunger();
        assertTrue("doesn't match", h.match(p.getClass()));
        h.mix(p);
        assertEquals("didn't afflict", 1, p.getAfflictions().size());

        int tries = 0;
        while(!p.isDead()) {
            p.tick();
            if(Hunger.Degree.degreeFor(p.getHunger())==Hunger.Degree.fainting) {
                assertTrue("did not start fainting", p.isAfflictedBy(Fainting.NAME));
            }
            if(++tries==10000) {
                fail("should have starved by now");
            }
        }
    }

    public void testPop() {
        Patsy p = new Patsy();
        Hunger h = new Hunger();
        assertFalse("matches", h.match(getClass()));
        h.mix(p);
        p.setHunger(-Hunger.RATE);
        p.setHunger(0);
        p.setHunger(-Integer.MAX_VALUE);
        assertTrue("how about an after-dinner mint?", p.isDead());
    }
}
