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
package org.excelsi.aether;


import org.excelsi.matrix.Direction;


public class TestPatsy extends junit.framework.TestCase {
    public void testDrop() {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        Patsy p = new Patsy();
        p.setForm(new TestDefaultNHBot.Handed());
        Item i = TestItem.createItem("ionizes", "atomizes");
        p.getInventory().add(i);
        Patsy.Drop d = new Patsy.Drop();
        d.setBot(p);
        lev.getSpace(5,5).setOccupant(p);
        d.perform();
        assertEquals("didn't remove", 0, p.getInventory().size());
        assertEquals("didn't add", 1, p.getEnvironment().getMSpace().getLoot().size());
        assertEquals("didn't add depth", -1, p.getEnvironment().getMSpace().getOccupantDepth());
    }

    public void testAirborn() {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        Patsy p = new Patsy();
        p.setForm(new TestDefaultNHBot.Handed());
        lev.getSpace(5,5).setOccupant(p);

        assertEquals("no tare", 0, p.getEnvironment().getMSpace().getOccupantDepth());
        p.setAirborn(true);
        assertEquals("not floating", -6, p.getEnvironment().getMSpace().getOccupantDepth());
    }

    public void testMovement() throws Exception {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        Patsy p = new Patsy();
        p.setForm(new TestDefaultNHBot.Handed());
        lev.getSpace(5,5).setOccupant(p);
        Class[] moves = new Class[]{Patsy.East.class, Patsy.West.class, Patsy.North.class, Patsy.South.class,
            Patsy.Northeast.class, Patsy.Northwest.class, Patsy.Southeast.class, Patsy.Southwest.class,
            Patsy.StrafeLeft.class, Patsy.StrafeRight.class, Patsy.Left.class, Patsy.Right.class};
        for(Class c:moves) {
            NHBotAction a = (NHBotAction) c.newInstance();
            a.setBot(p);
            try {
                a.perform();
            }
            catch(ActionCancelledException good) {
            }
        }
        assertEquals("didn't return to origin", p, lev.getSpace(5,5).getOccupant());
    }

    public void testActions() {
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        Patsy p = new Patsy();
        p.setForm(new TestDefaultNHBot.Handed());
        lev.getSpace(5,5).setOccupant(p);
        NHBotAction a = new Patsy.Look();
        a.setBot(p);
        a.perform();
        a = new Patsy.Rest();
        a.setBot(p);
        a.perform();
    }

    public void testOpen() {
        Level lev = new Level(20, 20);
        Level.Room r = new Level.Room(10, 10, 6, 6, 20, 20);
        r.setDoors(true);
        r.setWalled(true);
        lev.addRoom(r, true);
        Patsy p = new Patsy();
        p.setForm(new TestDefaultNHBot.Handed());
        lev.getSpace(10,10).setOccupant(p);
        Doorway d = new Doorway(true, false);
        lev.setSpace(d, 10, 7);
        p.getEnvironment().face(Direction.north);
        int tick = 0;
        while(!(p.getEnvironment().getMSpace().move(Direction.north) instanceof Doorway)) {
            p.getEnvironment().forward();
            if(++tick>20) {
                fail("shouldn't take that long");
            }
        }
        Patsy.Open o = new Patsy.Open();
        o.setBot(p);
        o.perform();
        assertTrue("didn't open", d.isOpen());
        o.perform();
        assertFalse("didn't close", d.isOpen());

        Patsy.Backward w = new Patsy.Backward();
        w.setBot(p);
        w.perform();
        //p.getEnvironment().backward();
        try {
            o.perform();
            fail("no door here");
        }
        catch(ActionCancelledException good) {
        }
    }

    public void testTimelessActions() {
        GameAction g = new Patsy.InventoryAction();
        try {
            g.perform();
            fail("no time");
        }
        catch(ActionCancelledException good) {
        }
        g = new Patsy.Skills();
        try {
            g.perform();
            fail("no time");
        }
        catch(ActionCancelledException good) {
        }
    }
}
