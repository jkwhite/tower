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


public class TestRewiring extends junit.framework.TestCase {
    public void testBlessed() {
        Rewiring r = new Rewiring();
        Patsy p = new Patsy();
        r.setStatus(Status.blessed);
        r.inflict(p);
        assertEquals("didn't get affliction", 1, p.getAfflictions().size());
        int tries = 0;
        while(p.getAfflictions().size()>0) {
            p.tick();
            if(++tries==1000) {
                fail("should have cured by now");
            }
        }
    }

    public void testUncursed() {
        Rewiring r = new Rewiring();
        Patsy p = new Patsy();
        r.inflict(p);
        assertEquals("didn't get affliction", 1, p.getAfflictions().size());
        int tries = 0;
        while(p.getAfflictions().size()>0) {
            p.tick();
            if(++tries==1000) {
                fail("should have cured by now");
            }
        }
    }

    public void testCursed() {
        Rewiring r = new Rewiring();
        r.setStatus(Status.cursed);
        Patsy p = new Patsy();
        r.inflict(p);
        assertEquals("got affliction", 0, p.getAfflictions().size());
    }
}
