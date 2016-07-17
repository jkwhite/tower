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


public class TestSpaces extends junit.framework.TestCase {
    static final NHSpace[] tests = new NHSpace[]{new Shrine(), new Basin(), new Fabricator(),
        new Grass(0), new Grass(1), new Grass(2), new Grass(3), new Water(4, 0)};

    public void testValues() {
        for(NHSpace s:tests) {
            assertNotNull("null "+s, s.getModel());
            assertNotNull("null "+s, s.getColor());
            //have to do something or jit might remove
            System.err.print(s.isWalkable());
            System.err.print(s.isTransparent());
            System.err.print(s.isStretchy());
        }
    }

    public void testLook() {
        Patsy p = new Patsy();
        for(NHSpace s:tests) {
            s.setOccupant(p);
            s.look(p);
        }
    }

    public void testGrass() {
        try {
            Grass g = new Grass(1337);
            fail("that is not all0wed. damn it, now you've got m3 doing it.");
        }
        catch(IllegalArgumentException good) {
        }
    }
}
