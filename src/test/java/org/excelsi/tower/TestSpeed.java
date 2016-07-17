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


public class TestSpeed extends junit.framework.TestCase {
    public void testDosage() {
        Speed s = new Speed();
        s.setDosage(0.5f);
        int d = s.dose(1);
        assertTrue("weird dosage", d==0||d==1);
    }

    public void testCursed() {
        Speed s = new Speed();
        s.setStatus(Status.cursed);
        Patsy p = new Patsy();
        p.setQuickness(10);
        s.inflict(p);
        assertTrue("did not get quickness bonus", p.getModifiedQuickness()>10);
        int tries = 0;
        while(p.isAfflictedBy("speed")) {
            p.tick();
            if(++tries==1000) {
                fail("shouldn't take that long to heal");
            }
        }
        assertEquals("did not restore quickness", 10, p.getModifiedQuickness());
    }

    public void testUncursed() {
        Speed s = new Speed();
        Patsy p = new Patsy();
        p.setQuickness(10);
        s.inflict(p);
        assertTrue("did not get quickness bonus", p.getModifiedQuickness()>10);
    }

    public void testBlessed() {
        Speed s = new Speed();
        s.setStatus(Status.blessed);
        Patsy p = new Patsy();
        p.setQuickness(10);
        s.inflict(p);
        assertTrue("did not get quickness bonus", p.getModifiedQuickness()>10);
    }
}
