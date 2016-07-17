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


public class TestShrine extends junit.framework.TestCase {
    public void testAddRemove() {
        Shrine s = new Shrine();
        Patsy p = new Patsy();
        assertTrue("shrine not empty", s.isEmpty());
        Cake c = new Cake();
        s.enshrine(c, p);

        Toast t = new Toast();
        try {
            s.enshrine(t, p);
            fail("toast does not beat cake");
        }
        catch(IllegalStateException good) {
        }

        assertFalse("cake mysteriously vanished", s.isEmpty());
        s.pickup(p);
        assertEquals("cake mysteriously transformed", c, p.getInventory().getItem()[0]);
    }

    public void testChangeStatusInside() {
        Pineapple pa = new Pineapple();
        pa.setStatus(Status.cursed);

        Patsy p = new Patsy();
        Shrine s = new Shrine();
        s.enshrine(pa, p);
        for(int i=0;i<=Shrine.TIME;i++) {
            Time.tick();
            s.update();
        }
        assertEquals("pineapple was not uncursed", Status.uncursed, pa.getStatus());
    }

    public void testCycleStatus() {
        Pineapple pa = new Pineapple();
        pa.setStatus(Status.cursed);

        Patsy p = new Patsy();
        Shrine s = new Shrine();
        s.enshrine(pa, p);
        for(int i=0;i<=2*Shrine.TIME;i++) {
            Time.tick();
        }
        s.update();
        assertEquals("pineapple was not blessed", Status.blessed, pa.getStatus());
        for(int i=0;i<=Shrine.TIME;i++) {
            Time.tick();
        }
        s.update();
        assertEquals("pineapple was not cursed", Status.cursed, pa.getStatus());
    }

    public void testLook() {
        Patsy p = new Patsy();
        Shrine s = new Shrine();
        s.look(p);
        s.enshrine(new Cake(), p);
        s.look(p);
        s.add(new MortarAndPestle());
        s.look(p);
    }

    public void testChangeStatusRemove() {
        Pineapple pa = new Pineapple();
        pa.setStatus(Status.cursed);

        Patsy p = new Patsy();
        Shrine s = new Shrine();
        s.enshrine(pa, p);
        for(int i=0;i<=Shrine.TIME;i++) {
            Time.tick();
        }
        s.pickup(p);
        assertEquals("pineapple was not uncursed", Status.uncursed, pa.getStatus());
    }

    public void testCycle() {
        Pineapple pa = new Pineapple();
        pa.setStatus(Status.cursed);

        Patsy p = new Patsy();
        Shrine s = new Shrine();
        s.enshrine(pa, p);
        for(int i=0;i<=Shrine.TIME;i++) {
            Time.tick();
        }
        s.pickup(p);

        FoodRation fr = new FoodRation();
        s.enshrine(fr, p);
        for(int i=0;i<=Shrine.TIME;i++) {
            Time.tick();
        }
        s.pickup(p);
        assertEquals("foodration was not blessed", Status.blessed, fr.getStatus());
    }
}
