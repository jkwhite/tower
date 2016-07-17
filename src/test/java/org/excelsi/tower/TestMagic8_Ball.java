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


public class TestMagic8_Ball extends junit.framework.TestCase {
    public void testHidden() {
        assertFalse("no", new Magic8_Ball().isObtainable());
    }

    public void testInvoke() {
        String text = "println 'the pillows - advice'";
        Evaluate e = new Evaluate(text);
        e.setBot(new Patsy());
        e.perform();
        new Magic8_Ball().invoke(new Patsy());
        new Magic8_Ball().invoke(null, (NHBot)null, null);
        assertEquals("not special", 0, new Magic8_Ball().getModifiedPower());
        assertEquals("identity", new Magic8_Ball(), new Magic8_Ball().toItem());
    }
}
