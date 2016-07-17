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
import java.util.ArrayList;
import org.excelsi.matrix.*;


public class TestWandOfDigging extends junit.framework.TestCase {
    public void testDig() {
        final StringBuilder events = new StringBuilder();
        final String[] R1 = {"rose", "through", "city", "there's", "and", "and"};
        final String[] R2 = {"up", "the", "lights", "stars", "moons", "satellites"};
        Level lev = new Level(20, 20);
        for(int i=0;i<WandOfDigging.LENGTH;i++) {
            lev.setSpace(new Wall(), i+1, 1);
        }
        Ground g = new Ground();
        lev.setSpace(g, 0, 1);
        lev.addListener(new MatrixListener() {
            public void spacesRemoved(Matrix m, MSpace[] spaces) {
                events.append(" "+R1[((MatrixMSpace)spaces[0]).getI()-1]);
            }

            public void spacesAdded(Matrix m, MSpace[] spaces) {
                events.append(" "+R2[((MatrixMSpace)spaces[0]).getI()-1]);
            }

            public void attributeChanged(Matrix m, String attr, Object oldValue, Object newValue) {
            }
        });
        Patsy p = new Patsy();
        g.setOccupant(p);
        WandOfDigging w = new WandOfDigging();
        w.dig(p, Direction.east);
        assertEquals("did not get events",
            "rose up through the city lights there's stars and moons and satellites",
            events.toString().trim());
    }

    public void testAuto() {
        Level lev = new Level(20, 20);
        lev.setSpace(new Floor(), 2,2);
        lev.setSpace(new Floor(), 1,2);
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        lev.getSpace(2,2).setOccupant(p);
        WandOfDigging w = new WandOfDigging();
        w.invoke(p);
        w.setCharges(0);
        lev.addListener(new MatrixListener() {
            public void spacesRemoved(Matrix m, MSpace[] spaces) {
                fail("no charges");
            }

            public void spacesAdded(Matrix m, MSpace[] spaces) {
                fail("no charges");
            }

            public void attributeChanged(Matrix m, String attr, Object oldValue, Object newValue) {
            }
        });
        w.invoke(p);
    }
}
