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


public class TestCyanide extends junit.framework.TestCase {
    public void testCursed() {
        Cyanide s = new Cyanide();
        s.setStatus(Status.cursed);
        Patsy paul = new Patsy();
        s.inflict(paul);
        assertTrue("didn't die", paul.isDead());
    }

    public void testUncursed() {
        Cyanide s = new Cyanide();
        Patsy p = new Patsy();
        s.inflict(p);
        assertTrue("didn't die", p.isDead());
    }

    public void testBlessed() {
        Rand.load();
        Cyanide s = new Cyanide();
        s.setStatus(Status.blessed);
        Patsy p = new Patsy();
        s.inflict(p);
        assertTrue("didn't die", p.isDead());
    }

    public void testOccur() {
        assertEquals("shouldn't be able to find it", 0, new Cyanide().getOccurrence());
    }
}
