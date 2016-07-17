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


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Direction;
import org.excelsi.aether.*;


public class Vine extends Plant {
    public Size getSize() {
        return Size.small;
    }

    protected Item fruit() {
        return new Blackberry();
    }

    protected String getName() {
        return "blackberry vine";
    }

    protected String getNormalColor() {
        return "dark-green";
    }

    protected long getLifespan() {
        return 8000L;
    }

    protected int getSpawnTime() {
        return 800;
    }

    public boolean canLeave(MSpace to) {
        NHBot b = getSpace().getOccupant();
        if(Rand.d100(b.getModifiedAgility())) {
            return true;
        }
        else {
            N.narrative().printf(b, "%v entangled in a vine!");
            for(Item i:b.getWearing()) {
                if(i instanceof Tearable && Rand.d100(25)) {
                    ((Tearable)i).tear(b, new Source("vine"), b.getInventory());
                }
            }
            return false;
        }
    }
}
