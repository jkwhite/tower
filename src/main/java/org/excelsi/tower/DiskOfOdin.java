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


public class DiskOfOdin extends Gem implements Catalyst {
    public DiskOfOdin() {
        setName("Disk of Odin");
    }

    public Item internalCatalyze() {
        return new ScrollOfVacuumMetastabilityDisaster();
    }

    public String getName() {
        if(isClassIdentified()) {
            return super.getName();
        }
        else {
            return "translucent disk";
        }
    }

    public float getSize() {
        return 1f;
    }

    public float getWeight() {
        return 0f;
    }

    public String getColor() {
        return "transparent";
    }

    public int getFindRate() {
        return 0;
    }

    public boolean isUnique() {
        return true;
    }

    public boolean isWishable() {
        return false;
    }

    public Stat[] getStats() {
        return null;
    }
}
