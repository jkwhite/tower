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


public class TestBasis extends junit.framework.TestCase {
    public void testClaim() {
        // must claim outside normal range because this is all static
        // and the test could be running in a jvm used by another test
        // as well.
        Basis.claim(new Basis(Basis.Type.swords, 200));
        try {
            Basis.claim(new Basis(Basis.Type.swords, 200));
            fail("allowed identical claim");
        }
        catch(IllegalArgumentException good) {
        }
    }

    public void testString() {
        assertEquals("science", "6 of swords (stable)", new Basis(Basis.Type.swords, 6).toString());
        assertEquals("change", "2 of disks (unstable)", new Basis(Basis.Type.disks, 2, false, Basis.State.unstable).toString());
    }
}
