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
 * Maneuver represents the degree of difficulty associated with some task.
 */
public enum Maneuver {
    routine(30),
    light(15),
    medium(0),
    hard(-15),
    veryhard(-30),
    folly(-45),
    absurd(-60);


    private int _bonus;


    /**
     * Gets the bonus for this degree of difficulty.
     *
     * @return bonus or penalty
     */
    public int getBonus() {
        return _bonus;
    }

    private Maneuver(int bonus) {
        _bonus = bonus;
    }
}
