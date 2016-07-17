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


public class TestFrenzy extends junit.framework.TestCase {
    public void testCursed() {
        Frenzy s = new Frenzy();
        s.setStatus(Status.cursed);
        Patsy p = new Patsy();
        p.setStrength(10);
        p.setHp(10);
        p.setMaxHp(10);
        s.inflict(p);
        assertTrue("did not get strength boost", p.getModifiedStrength()>10);
        assertTrue("did not get hp boost", p.getHp()>10);
        assertTrue("did not get confused", p.getAfflictions().get(0) instanceof Frenzied);
        assertTrue("did not get frenzy", p.getAfflictions().get(1) instanceof Confused);
        assertTrue("did not get overdose", p.getAfflictions().get(2) instanceof Overdose);
        tillEnd(p);
    }

    public void testUncursed() {
        Frenzy s = new Frenzy();
        Patsy p = new Patsy();
        p.setStrength(10);
        p.setHp(10);
        p.setMaxHp(10);
        s.inflict(p);
        assertTrue("did not get strength boost", p.getModifiedStrength()>10);
        assertTrue("did not get hp boost", p.getHp()>10);
        assertTrue("did not get frenzy", p.getAfflictions().get(0) instanceof Frenzied);
        assertTrue("did not get overdose", p.getAfflictions().get(1) instanceof Overdose);
        tillEnd(p);
    }

    public void testBlessed() {
        Frenzy s = new Frenzy();
        s.setStatus(Status.blessed);
        Patsy p = new Patsy();
        p.setStrength(10);
        p.setHp(10);
        p.setMaxHp(10);
        s.inflict(p);
        assertTrue("did not get strength boost", p.getModifiedStrength()>10);
        assertTrue("did not get hp boost", p.getHp()>10);
        assertTrue("did not get frenzy", p.getAfflictions().get(0) instanceof Frenzied);
        assertTrue("did not get overdose", p.getAfflictions().get(1) instanceof Overdose);
        tillEnd(p);
    }

    public void testTime() {
        assertEquals("time", 21, new Frenzied(new Modifier(), 1, 21).getRemaining());
        assertEquals("name", "Frenzy", new Frenzied(new Modifier(), 1, 21).getStatus());
    }

    private void tillEnd(Patsy p) {
        int tries = 0;
        while(p.getAfflictions().size()>0) {
            p.tick();
            if(++tries==1000) {
                fail("shouldn't take that long to heal");
            }
        }
        assertTrue("did not restore hp", p.getHp()==10);
        assertTrue("did not restore strength", p.getModifiedStrength()==10);
    }
}
