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


import java.util.List;


public interface MSpace extends Typed, java.io.Serializable {
    MSpace move(Direction direction);
    MSpace move(Direction direction, boolean anull);
    Direction directionTo(MSpace neighbor);
    MSpace[] surrounding();
    MSpace[] surrounding(boolean anull);
    MSpace[] surrounding(MSpace[] sur, boolean anull);
    MSpace[] cardinal();
    MSpace[] cardinal(boolean anull);
    MSpace[] cardinal(MSpace[] cards, boolean anull);
    boolean isWalkable();
    boolean isTransparent();
    boolean isStretchy();
    boolean isNull();
    boolean visibleFrom(MSpace other, float max);
    float distance(MSpace other);
    boolean isCardinalTo(MSpace other);
    boolean isDiagonalTo(MSpace other);
    boolean isAdjacentTo(MSpace other);
    Bot getOccupant();
    boolean isOccupied();
    void clearOccupant();
    void setOccupant(Bot b);
    void moveOccupant(MSpace to);
    void swapOccupant(MSpace other);
    void addMSpaceListener(MSpaceListener listener);
    void removeMSpaceListener(MSpaceListener listener);
    List<MSpaceListener> getMSpaceListeners();
    MSpace creator();
    boolean isReplaceable();
    MSpace replace(MSpace replacement);
    boolean push(MSpace pusher, Direction dir);
    MSpace closest(Filter filter, boolean cardinal);
    MSpace[] path(MSpace to, boolean cardinal, Filter filter, float sanity);
    MSpace[] path(MSpace to, boolean cardinal, Filter filter, float sanity, Affinity a);
    MSpace[][] paths(MSpace to, boolean cardinal, int count, Filter filter, float sanity, Affinity a);
    void update();
    Environs getEnvirons();
}
