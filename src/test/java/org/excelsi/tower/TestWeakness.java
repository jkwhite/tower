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


public class TestWeakness extends junit.framework.TestCase {
    public void testCursed() {
        Weakness s = new Weakness();
        s.setStatus(Status.cursed);
        Patsy p = new Patsy();
        p.setStrength(10);
        s.inflict(p);
        assertTrue("did not get strength penalty", p.getStrength()<10);
        assertTrue("did not get overdose", p.getAfflictions().get(0) instanceof Overdose);
        int tries = 0;
        while(p.isAfflictedBy("weakness")) {
            p.tick();
            if(++tries==1000) {
                fail("shouldn't take that long to heal");
            }
        }
        assertTrue("did not get partial strength restore", p.getStrength()>=5);
    }

    public void testUncursed() {
        Weakness s = new Weakness();
        Patsy p = new Patsy();
        p.setStrength(10);
        s.inflict(p);
        assertTrue("did not get strength penalty", p.getStrength()<10);
    }

    public void testBlessed() {
        Weakness s = new Weakness();
        s.setStatus(Status.blessed);
        Patsy p = new Patsy();
        p.setStrength(10);
        s.inflict(p);
        assertTrue("did not get strength penalty", p.getStrength()<10);
    }
}
