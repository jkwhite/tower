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


public class Sling extends Weapon2H {
    public float getLevelWeight() { return 0.1f; }

    public Type getType() {
        return Type.missile;
    }

    public String getSkill() {
        return "sling";
    }

    public int getPower() {
        return 3;
    }

    public int getRate() {
        return 70;
    }

    public float getWeight() {
        return 1f;
    }

    public float getSize() {
        return 1f;
    }

    public String getColor() {
        return "light-brown";
    }
}
