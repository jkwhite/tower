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
import java.util.ArrayList;
import java.util.List;
import java.util.EnumSet;


public enum Direction {
    north(0, -1),
    south(0, 1),
    east(1, 0),
    west(-1, 0),
    northeast(1, -1),
    southeast(1, 1),
    southwest(-1, 1),
    northwest(-1, -1),
    up(0, 0, 1),
    down(0, 0, -1);


    private final int[] _v;
    private final float _d;


    Direction(int x, int y) {
        this(x, y, 0);
    }

    Direction(int x, int y, int z) {
        _v = new int[]{x, y, z};
        _d = (float) Math.sqrt(x*x+y*y+z*z);
    }

    public int[] vector() {
        return _v;
    }

    public float distance() {
        return _d;
    }

    public Direction left() {
        switch(this) {
            case north:
                return northwest;
            case northeast:
                return north;
            case east:
                return northeast;
            case southeast:
                return east;
            case south:
                return southeast;
            case southwest:
                return south;
            case west:
                return southwest;
            case northwest:
                return west;
            case up:
                return down;
            case down:
                return up;
            default:
                throw new Error();
        }
    }

    public Direction right() {
        switch(this) {
            case north:
                return northeast;
            case northeast:
                return east;
            case east:
                return southeast;
            case southeast:
                return south;
            case south:
                return southwest;
            case southwest:
                return west;
            case west:
                return northwest;
            case northwest:
                return north;
            case up:
                return down;
            case down:
                return up;
            default:
                throw new Error();
        }
    }

    public Direction opposing() {
        switch(this) {
            case north:
                return south;
            case northeast:
                return southwest;
            case east:
                return west;
            case southeast:
                return northwest;
            case south:
                return north;
            case southwest:
                return northeast;
            case west:
                return east;
            case northwest:
                return southeast;
            case up:
                return down;
            case down:
                return up;
            default:
                throw new Error();
        }
    }

    public boolean isCardinal() {
        return _v[0]==0||_v[1]==0;
    }

    private static final List<Direction> ALL_DIRS = new ArrayList<Direction>(EnumSet.of(Direction.north, Direction.northeast,
        Direction.northwest, Direction.west, Direction.east, Direction.south, Direction.southwest, Direction.southeast));
    public static Direction random() {
        return ALL_DIRS.get(Matrix._rand.nextInt(ALL_DIRS.size()));
    }
}
