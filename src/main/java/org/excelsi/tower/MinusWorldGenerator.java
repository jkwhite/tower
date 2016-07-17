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
import org.excelsi.matrix.MatrixMSpace;


public class MinusWorldGenerator extends AbstractLevelGenerator {
    public MinusWorldGenerator(int maxWidth, int maxHeight) {
        super(maxWidth,maxHeight);
    }

    protected void modulate(Level.Room room) {
        room.setWalled(false);
        room.setFloorClass(Grass.class);
    }

    public void generate(Level level, MatrixMSpace player) {
        super.generate(level);

        int x = width()/2, y=height()/2;
        Level.Room r = new Level.Room(x, y, 5, 5, width(), height());
        modulate(r);
        level.addRoom(r, true);
        level.getSpace(x, y).replace(new Stairs(true));
        level.getSpace(r.getX1()+1, r.getY1()+1).replace(new Fabricator(true));
    }
}
