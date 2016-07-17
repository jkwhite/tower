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


public class TestAdvancedBot extends junit.framework.TestCase {
    public void testDeference() {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        AdvancedBot b = new AdvancedBot();
        b.setCommon("us");
        lev.getSpace(5,5).setOccupant(b);
        Patsy p = new Patsy();
        p.setCommon("them");
        b.setThreat(p, Threat.kos);
        lev.getSpace(5,4).setOccupant(p);
        p.setStrength(Integer.MAX_VALUE);
        lev.tick();
        lev.tick();
        //TODO
        //assertTrue("didn't activate flight", b.getAI().FLIGHT.isActive());
    }
}
