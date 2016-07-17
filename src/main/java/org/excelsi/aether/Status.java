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
 * Status indicates the cursed/uncursed/blessed state of something.
 * Status may be indicated visually by color.
 */
public enum Status {
    /** cursed state */
    cursed("black"),
    /** uncursed state */
    uncursed("yellow"),
    /** blessed state */
    blessed("white");


    /** color of this status */
    private String _color;


    /**
     * Constructs a new status.
     *
     * @param color status color
     */
    private Status(String color) {
        _color = color;
    }

    /**
     * Gets the color of this status, i.e. <code>white</code>.
     *
     * @return color
     */
    public String getColor() {
        return _color;
    }

    /**
     * Returns the status just worse than this. From best to worst,
     * status is: blessed, uncursed, cursed.
     *
     * @return next worse status, or worst status if status is already worst
     */
    public Status worse() {
        switch(this) {
            case cursed:
            case uncursed:
                return cursed;
            case blessed:
                return uncursed;
        }
        throw new Error();
    }

    /**
     * Returns the status just better than this. From best to worst,
     * status is: blessed, uncursed, cursed.
     *
     * @return next best status, or best status if status is already best
     */
    public Status better() {
        switch(this) {
            case cursed:
                return uncursed;
            case uncursed:
            case blessed:
                return blessed;
        }
        throw new Error();
    }

    /**
     * Gets a random status. All statuses
     * are returned with equal probability.
     *
     * @return random status
     */
    public static Status random() {
        switch(Rand.om.nextInt(3)) {
            case 0:
                return cursed;
            case 1:
                return uncursed;
            default:
                return blessed;
        }
    }
};
