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
import java.util.Arrays;
import java.util.ArrayList;
import org.excelsi.matrix.Direction;
import org.excelsi.matrix.MSpace;


public class TestDig extends junit.framework.TestCase {
    public void testPickaxe() {
        digTest(new Pick_Axe());
    }

    public void testJackhammer() {
        digTest(new Jackhammer());
    }

    public void testFail() throws EquipFailedException {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        Pick_Axe t = new Pick_Axe();
        p.getInventory().add(t);
        Broadsword s = new Broadsword();
        s.setStatus(Status.cursed);
        p.setWielded(s);
        try {
            t.invoke(p);
            fail("shouldn't be able to wield pick-axe");
        }
        catch(ActionCancelledException good) {
        }
    }

    public void testAuto() {
        Level lev = new Level(20, 20);
        lev.setSpace(new Ground(), 1, 1);
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        Pick_Axe t = new Pick_Axe();
        p.getInventory().add(t);
        lev.getSpace(1, 1).setOccupant(p);
        t.invoke(p);

        int ticks = 0;
        while(p.isOccupied()) {
            lev.tick();
            if(++ticks>20) {
                fail("shouldn't take that long");
            }
        }
        MSpace m = lev.getSpace(1,1).move(Direction.north);
        assertNotNull("didn't create space", m);
        assertTrue("wrong type: "+m, m instanceof Ground);
    }

    private void digTest(Pick_Axe t) {
        Level lev = new Level(20, 20);
        lev.setSpace(new Ground(), 1, 1);
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        p.getInventory().add(t);
        lev.getSpace(1, 1).setOccupant(p);
        t.dig(p, Direction.east);
        int ticks = 0;
        while(p.isOccupied()) {
            lev.tick();
            if(++ticks>20) {
                fail("shouldn't take that long");
            }
        }
        MSpace m = lev.getSpace(1,1).move(Direction.east);
        assertNotNull("didn't create space", m);
        assertTrue("wrong type: "+m, m instanceof Ground);
    }
}
