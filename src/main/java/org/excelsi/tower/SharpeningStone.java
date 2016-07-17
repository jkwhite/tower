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


import org.excelsi.aether.Rand;


public class SharpeningStone extends SmallStone {
    public SharpeningStone() {
        setClassIdentified(true);
    }

    public boolean canCatalyze() {
        return false;
    }

    public boolean isNatural() {
        return false;
    }

    public void randomize() {
        super.randomize();
        setCount(Rand.om.nextInt(5)+1);
    }

    public String getName() {
        return "sharpening stone";
    }

    public int getSharpeningPower() {
        return 10;
    }

    public int getFindRate() {
        return 20;
    }
}
