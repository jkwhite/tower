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


public class TestPolymorph extends junit.framework.TestCase {
    protected void setUp() {
        BasicBot b = new BasicBot();
        b.setForm(new Canid());
        b.setCommon("");
        b.setModel("i don't need no arms around me");
        b.setColor("i don't need no drugs to calm me");
        Universe.setUniverse(new Universe());
        Universe.getUniverse().add(DefaultNHBot.copy(b));

        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        p.setCommon("axe");
        p.setModel("i have seen the writing on the wall");
        p.setColor("don't think i need anything at all");
        Universe.getUniverse().add(DefaultNHBot.copy(p));
    }

    public void testBlessed() {
        Polymorph p = new Polymorph();
        p.setStatus(Status.blessed);
        Patsy patsy = new Patsy();
        patsy.setForm(new Humanoid());

        p.inflict(patsy);
        assertEquals("shouldn't get afflicted", 0, patsy.getAfflictions().size());
    }

    public void testUncursed() {
        Polymorph p = new Polymorph();
        Patsy patsy = new Patsy();
        patsy.setForm(new Humanoid());
        patsy.setCommon("axe");

        p.inflict(patsy);
        assertEquals("should get afflicted", 1, patsy.getAfflictions().size());
        int tries = 0;
        while(patsy.getAfflictions().size()>0) {
            patsy.tick();
            if(++tries==1000) {
                fail("shouldn't take that long");
            }
        }
        assertEquals("just one of my turns", "axe", patsy.getCommon());
    }

    public void testCursed() {
        Polymorph p = new Polymorph();
        p.setStatus(Status.cursed);
        Patsy patsy = new Patsy();
        patsy.setForm(new Humanoid());

        p.inflict(patsy);
        assertEquals("shouldn't get afflicted", 0, patsy.getAfflictions().size());
    }
}
