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
import org.excelsi.aether.Floor;
import org.excelsi.aether.Item;
import org.excelsi.aether.Container;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.Rand;


public class Sakura extends Floor {
    private static final long serialVersionUID = 1L;


    public Sakura() {
    }

    public String getModel() {
        return "'";
    }

    public String getColor() {
        return "pink";
    }

    public boolean isStretchy() {
        return true;
    }
}
