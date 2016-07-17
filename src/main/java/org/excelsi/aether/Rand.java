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

import java.util.Random;


/**
 * Random dice roll utility.
 */
public class Rand {
    /** random generator */
    public static final Random om = new Random();
    private static boolean _loaded = false;


    /**
     * Makes these dice into loaded dice.
     * You'll make all chance rolls now.
     */
    public static void load() {
        _loaded = true;
    }

    /**
     * Rolls d100.
     */
    public static int d100() {
        return 1+om.nextInt(100);
    }

    /**
     * Rolls percentage dice. This is equilvalent
     * to <code>Rand.d100()&lt;=chance</code>.
     *
     * @param chance chance of success
     * @return <code>true</code> if successful
     */
    public static boolean d100(int chance) {
        return _loaded?true:d100()<=chance;
    }

    private Rand() {
    }
}
