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


public class TDoorway extends Doorway {
    private static final long serialVersionUID = 1L;

    public TDoorway() {
        super();
    }

    public TDoorway(boolean vertical) {
        super(vertical);
    }

    public Item[] getIngredients() {
        Item[] ing = new Item[]{new Board(), new Nail()};
        ing[0].setCount(3);
        ing[1].setCount(7);
        return ing;
    }

    public String getCreationSkill() {
        return "construction";
    }

    public Maneuver getDifficulty() {
        return Maneuver.routine;
    }
}
