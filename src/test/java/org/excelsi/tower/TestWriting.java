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
import org.excelsi.matrix.MSpace;


public class TestWriting extends junit.framework.TestCase {
    public void testMix() {
        WritingMixin w = new WritingMixin(new String[]{
                "deep beneath the rolling waves", "in labyrinthine coral caves"});
        Rand.load();
        Level lev = new Level(20, 20);
        assertTrue("doesn't mix", w.match(lev.getClass()));
        w.mix(lev); // should do nothing
        lev.addRoom(new Level.Room(0, 0, 18, 18, 18, 18), true);
        w.mix(lev);
        boolean found = false;
        Patsy p = new Patsy();
        for(MSpace s:lev.spaces()) {
            if(s!=null) {
                NHSpace ns = (NHSpace) s;
                if(ns.getParasites().size()>0&&ns.getParasites().get(0) instanceof Writing) {
                    found = true;
                    ns.setOccupant(p);
                    break;
                }
            }
        }
        if(!found) {
            fail("didn't write anything");
        }
    }
}
