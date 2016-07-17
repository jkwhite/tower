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


public class HasteningBoots extends Shoes {
    public HasteningBoots() {
        setName("pair of hastening boots");
    }

    public float getLevelWeight() { return 0.25f; }

    public String getName() {
        return isClassIdentified()?super.getName():"pair of laced boots";
    }

    public int getPower() {
        return 3;
    }

    public int getRate() {
        return 60;
    }

    public float getWeight() {
        return 0.25f;
    }

    public float getSize() {
        return 2;
    }

    public String getSkill() {
        return CLOTH;
    }

    public int getFindRate() {
        return 8;
    }

    public Modifier getModifier() {
        Modifier m = new Modifier(0, 20);
        m.setAgility(10);
        return m;
    }

    public String getColor() {
        return "white";
    }
}
