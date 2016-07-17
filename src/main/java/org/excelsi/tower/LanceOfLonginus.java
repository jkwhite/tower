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


public class LanceOfLonginus extends PoleArm implements Sharpenable {
    private static final long serialVersionUID = 1L;


    public LanceOfLonginus() {
        initHp(50);
    }

    public String getName() {
        if(isClassIdentified()) {
            return "Lance of Longinus";
        }
        else {
            return "old lance";
        }
    }

    public boolean isUnique() {
        return true;
    }

    public float getSize() {
        return 9.5f;
    }

    public float getWeight() {
        return 7.5f;
    }

    public int getPower() {
        return 66;
    }

    public int getRate() {
        return 90;
    }

    public int getFindRate() {
        return 0;
    }
}
