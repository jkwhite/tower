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


public class TestItemBot extends junit.framework.TestCase {
    public void testHeal() {
        NHEnvironment.setMechanics(new QuantumMechanics());
        new Healing().setClassIdentified(false);
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        ItemBot b = new ItemBot();
        b.setQuickness(10);
        b.setForm(new Humanoid());
        b.setName("cotton casino");
        b.setMaxHp(100);
        b.setHp(1);
        b.getInventory().add(new Potion(new Healing()));
        lev.getSpace(5,5).setOccupant(b);
        Patsy p = new Patsy();
        p.setForm(new Slime());
        p.setQuickness(10);
        lev.getSpace(2,2).setOccupant(p);
        lev.tick();
        lev.tick();
        assertTrue("atomic rotary grinding god", b.getHp()>1);
        b.setHp(100);
        lev.tick();
    }
}
