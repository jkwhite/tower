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
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.matrix.MSpace;


public class TestSectionLevelGenerator extends junit.framework.TestCase {
    public void testGenerate() {
        Level lev = new Level(80, 24);
        SectionLevelGenerator g = new SectionLevelGenerator();
        g.generate(lev, null);
        boolean found = false;
        for(MSpace m:lev.spaces()) {
            if(m instanceof Stairs && ((Stairs)m).isAscending()) {
                found = true;
                break;
            }
        }
        if(!found) {
            fail("no stairs");
        }
    }

    public void testGenerateWithPlayer() {
        Level lev = new Level(80, 24);
        SectionLevelGenerator g = new SectionLevelGenerator();
        MatrixMSpace ms = new MatrixMSpace() {
            public boolean isTransparent() {
                return true;
            }

            public boolean isWalkable() {
                return true;
            }

            public void update() {
            }
        };
        ms.setI(10);
        ms.setJ(10);
        g.generate(lev, ms);
        boolean found = false;
        for(MSpace m:lev.spaces()) {
            if(m instanceof Stairs && ((Stairs)m).isAscending()) {
                found = true;
                break;
            }
        }
        if(!found) {
            fail("no stairs");
        }
    }
}
