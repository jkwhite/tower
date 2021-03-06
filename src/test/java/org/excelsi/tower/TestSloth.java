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


public class TestSloth extends junit.framework.TestCase {
    public void testDosage() {
        Sloth s = new Sloth();
        s.setDosage(0.5f);
        int d = s.dose(1);
        assertTrue("weird dosage", d==0||d==1);
    }

    public void testCursed() {
        Sloth s = new Sloth();
        s.setStatus(Status.cursed);
        Patsy p = new Patsy();
        p.setQuickness(10);
        s.inflict(p);
        assertTrue("did not get quickness penalty", p.getModifiedQuickness()<10);
        int tries = 0;
        while(p.isAfflictedBy("slow")) {
            p.tick();
            if(++tries==1000) {
                fail("shouldn't take that long to heal");
            }
        }
        assertEquals("did not restore quickness", 10, p.getModifiedQuickness());
    }

    public void testUncursed() {
        Sloth s = new Sloth();
        Patsy p = new Patsy();
        p.setQuickness(10);
        s.inflict(p);
        assertTrue("did not get quickness penalty", p.getModifiedQuickness()<10);
    }

    public void testBlessed() {
        Sloth s = new Sloth();
        s.setStatus(Status.blessed);
        Patsy p = new Patsy();
        p.setQuickness(10);
        s.inflict(p);
        assertTrue("did not get quickness penalty", p.getModifiedQuickness()<10);
    }
}
