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
package org.excelsi.matrix;


public class NullMatrixMSpace extends MatrixMSpace {
    public NullMatrixMSpace() {
    }

    public NullMatrixMSpace(Matrix m, int i, int j) {
        setMatrix(m);
        setI(i);
        setJ(j);
    }

    public boolean isReplaceable() {
        return true;
    }

    public boolean isNull() {
        return true;
    }

    public boolean isWalkable() {
        return false;
    }

    public Bot getOccupant() {
        return null;
    }

    public boolean isOccupied() {
        return false;
    }

    public boolean isTransparent() {
        return false;
    }

    public void clearOccupant() {
        throw new IllegalStateException("null spaces may not be occupied");
    }

    public void setOccupant(Bot b) {
        throw new IllegalStateException("null spaces may not be occupied");
    }

    public void update() {
    }
}
