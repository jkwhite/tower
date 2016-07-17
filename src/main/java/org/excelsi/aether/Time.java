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


/**
 * Time is something that everyone experiences but no one can fully explain.
 */
public final class Time {
    /** now */
    private static long _now = 0;


    /**
     * Moves one step into the future, making it the present.
     * And not the future. I mean, you don't actually move
     * into the future, but the future ... <i>comes to you</i>.
     * Modern convenience.
     */
    public static void tick() {
        if(++_now==Long.MAX_VALUE) {
            throw new IllegalStateException("You've reached the end of the universe. Thanks for playing!");
        }
    }

    /**
     * Sets the current time.
     */
    static void setTime(long time) {
        _now = time;
    }

    /**
     * Gets the current time. The value of time increases monotonically.
     *
     * @return time
     */
    public static long now() {
        return _now;
    }

    private Time() {
    }
}
