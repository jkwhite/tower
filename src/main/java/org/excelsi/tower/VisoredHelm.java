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


public class VisoredHelm extends PlateHelmet {
    public float getLevelWeight() { return 0.6f; }

    public String getColor() {
        return "cyan";
    }

    public float getSize() {
        return 1;
    }

    public float getWeight() {
        return 2f;
    }

    public int getRate() {
        return 70;
    }

    public int getPower() {
        return 20;
    }

    public int getFindRate() {
        return 20;
    }

    public Modifier getModifier() {
        return new Modifier(0, -5);
    }
}
