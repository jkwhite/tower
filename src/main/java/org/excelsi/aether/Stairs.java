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


import org.excelsi.matrix.*;


public class Stairs extends DefaultNHSpace implements Climbable, Flooring {
    private static final long serialVersionUID = 1L;

    public boolean _ascending;
    private int _partition;


    public Stairs(boolean ascending) {
        this(ascending, 0);
    }

    public Stairs(boolean ascending, int partition) {
        super("gray");
        _ascending = ascending;
        _partition = partition;
    }

    public int getPartition() {
        return _partition;
    }

    public String getModel() {
        return _ascending?"<":">";
    }

    @Override public boolean look(Context c, boolean nothing, boolean lootOnly) {
        boolean any  = super.look(c, false, lootOnly);
        if(!lootOnly) {
            c.n().print(this, "There is a"+(_ascending?"n ascending":" descending")+" staircase here.");
            return true;
        }
        return any;
    }

    public boolean isWalkable() {
        return true;
    }

    public boolean isTransparent() {
        return true;
    }

    public boolean isAscending() {
        return _ascending;
    }

    public boolean isDescending() {
        return !isAscending();
    }

    public MSpace findEndpoint(Matrix m) {
        return null; // let game take care of it
    }

    public String getName() {
        return "staircase";
    }

    @Override public Orientation getOrientation() {
        return Orientation.upright;
    }
}
