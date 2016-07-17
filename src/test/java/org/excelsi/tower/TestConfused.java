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
import org.excelsi.matrix.Direction;


public class TestConfused extends junit.framework.TestCase {
    public void testMovement() {
        Floor f = new Floor();
        Confused c = new Confused();
        Patsy p = new Patsy();
        f.setOccupant(p);
        p.addAffliction(c);
        boolean found = false;
        Direction d = p.getEnvironment().getFacing();
        for(int tries=0;tries<20;tries++) {
            c.beset();
            if(p.getEnvironment().getFacing()!=d) {
                found = true;
                break;
            }
        }
        if(!found) {
            fail("should have been disoriented by now");
        }
        while(c.getRemaining()>0) {
            c.beset();
            c.tick();
        }
        assertFalse("didn't cure over time", p.isAfflictedBy(Confused.NAME));
    }

    public void testDeath() {
        Floor f = new Floor();
        Confused c = new Confused();
        Patsy p = new Patsy();
        f.setOccupant(p);
        p.addAffliction(c);
        p.die("Killed by divine whimsy");
        assertEquals("wrong excuse", "Killed by divine whimsy, while confused.", p.getDeath());
    }
}
