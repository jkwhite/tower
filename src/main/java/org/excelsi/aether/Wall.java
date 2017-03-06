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
package org.excelsi.aether;


import org.excelsi.matrix.*;


public class Wall extends DefaultNHSpace {
    private static final long serialVersionUID = 1L;
    //private static MatrixListener _wallListener;
    private int _lastType = -1;

    public static final int HORIZ = 0;
    public static final int VERT = 1;
    public static final int UP_LEFT = 2;
    public static final int UP_RIGHT = 3;
    public static final int DOWN_LEFT = 4;
    public static final int DOWN_RIGHT = 5;


    public MatrixListener getWallListener() {
        return new MatrixListener() {
            public void spacesRemoved(Matrix m, MSpace[] spaces, Bot b) {
                boolean any = false;
                for(MSpace s:spaces) {
                    if(s==Wall.this) {
                        m.removeListener(this);
                        return;
                    }
                    if(isCardinalTo(s)) {
                        any = true;
                    }
                }
                if(any) {
                    int newType = getType();
                    if(_lastType!=newType) {
                        notifyAttr("model", null, null);
                    }
                }
            }

            public void spacesAdded(Matrix m, MSpace[] spaces, Bot b) {
                boolean any = false;
                for(MSpace s:spaces) {
                    if(s==Wall.this) {
                        return;
                    }
                    if(isCardinalTo(s)) {
                        any = true;
                    }
                }
                if(any) {
                    int newType = getType();
                    if(_lastType!=newType) {
                        notifyAttr("model", null, null);
                    }
                }
            }

            public void attributeChanged(Matrix m, String attr, Object oldValue, Object newValue) {
            }
        };
    }

    public Wall() {
        super("gray");
    }

    public String getModel() {
        switch(getType()) {
            case HORIZ:
                return "-";
            case VERT:
                return "|";
            case UP_LEFT:
            case UP_RIGHT:
            case DOWN_RIGHT:
            case DOWN_LEFT:
                return "+";
        }
        throw new IllegalStateException("no wall type");
    }

    public void setMatrix(Matrix m) {
        super.setMatrix(m);
        m.addListener(getWallListener());
    }

    public int getType() {
        int type;
        MSpace n = move(Direction.north);
        MSpace s = move(Direction.south);
        MSpace e = move(Direction.east);
        MSpace w = move(Direction.west);
        if(isFloor(n) || isFloor(s)) {
            if(n instanceof Wall || s instanceof Wall) {
                if(e instanceof Wall || w instanceof Wall) {
                    type = UP_LEFT;
                }
                else {
                    type = VERT;
                }
            }
            else {
                type = HORIZ;
            }
        }
        else if(isFloor(w) || isFloor(e)) {
            if(w instanceof Wall || e instanceof Wall) {
                if(n instanceof Wall || s instanceof Wall) {
                    type = UP_LEFT;
                }
                else {
                    type = VERT;
                }
            }
            else {
                type = VERT;
            }
        }
        else if(isFloor(move(Direction.northeast))) {
            type = DOWN_LEFT;
        }
        else if(isFloor(move(Direction.northwest))) {
            type = DOWN_RIGHT;
        }
        else if(isFloor(move(Direction.southeast))) {
            type = UP_LEFT;
        }
        else if(isFloor(move(Direction.southwest))) {
            type = UP_RIGHT;
        }
        else {
            type = HORIZ;
        }
        _lastType = type;
        return type;
    }

    private static boolean isFloor(MSpace m) {
        return m instanceof Flooring;
    }

    public boolean isHorizontal() {
        return getType() == HORIZ;
    }

    public boolean isVertical() {
        return getType() == VERT;
    }

    public boolean isUpLeft() {
        return getType() == UP_LEFT;
    }

    public boolean isUpRight() {
        return getType() == UP_RIGHT;
    }

    public boolean isDownLeft() {
        return getType() == DOWN_LEFT;
    }

    public boolean isDownRight() {
        return getType() == DOWN_RIGHT;
    }

    public boolean isWalkable() {
        return false;
    }

    public boolean isTransparent() {
        return false;
    }

    public MatrixMSpace union(MatrixMSpace m) {
        if(m.getClass()==Wall.class) {
            return this;
        }
        else {
            return super.union(m);
        }
    }

    public boolean push(MSpace pusher, Direction dir) {
        if(pusher instanceof Wall) {
            return false;
        }
        else {
            return super.push(pusher, dir);
        }
    }
}
